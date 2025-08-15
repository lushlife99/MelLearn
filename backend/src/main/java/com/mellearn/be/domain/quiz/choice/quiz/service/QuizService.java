package com.mellearn.be.domain.quiz.choice.quiz.service;

import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.repository.MemberRepository;
import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.entity.Quiz;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.mellearn.be.domain.quiz.choice.quiz.repository.QuizListRepository;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitRequest;
import com.mellearn.be.domain.quiz.choice.submit.repository.QuizSubmitRepository;
import com.mellearn.be.domain.quiz.choice.submit.service.QuizSubmitService;
import com.mellearn.be.domain.quiz.listening.quiz.dto.ListeningQuizDto;
import com.mellearn.be.domain.quiz.listening.quiz.entity.ListeningQuiz;
import com.mellearn.be.domain.quiz.listening.quiz.repository.ListeningQuizRepository;
import com.mellearn.be.domain.quiz.listening.submit.dto.ListeningSubmitDto;
import com.mellearn.be.domain.quiz.listening.submit.dto.request.ListeningSubmitRequest;
import com.mellearn.be.global.error.CustomException;
import com.mellearn.be.global.error.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private static final String QUIZ_LIST_CACHE_KEY = "quizListCache";
    private static final int PAGE_SIZE = 10;

    private final QuizSubmitService quizSubmitService;
    private final QuizSubmitRepository quizSubmitRepository;
    private final QuizListRepository quizListRepository;
    private final ListeningQuizRepository listeningQuizRepository;
    private final MemberRepository memberRepository;

    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(readOnly = true)
    public List<?> getSubmitList(QuizType quizType, Long lastSeenId, String memberId) {
        Member member = findMember(memberId);

        if (quizType.equals(QuizType.LISTENING)) {
            return quizSubmitRepository.findListeningSubmitWithPaging(member.getId(), lastSeenId, PAGE_SIZE);
        } else if (quizType.equals(QuizType.SPEAKING)) {
            return quizSubmitRepository.findSpeakingSubmitWithPaging(member.getId(), lastSeenId, PAGE_SIZE);
        } else {
            return quizSubmitRepository.findSubmitWithPaging(member.getId(), quizType, lastSeenId, PAGE_SIZE);
        }
    }

    /**
     * redis - 이미 생성되고 있는 퀴즈를 확인하는 로직에서 동시성 제어 안됨.
     */
    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<QuizListDto> getQuizList(QuizRequest quizRequest) {

        // 1. 캐시에서 값 조회
        String quizListKey = String.join("_",
                quizRequest.getMusicId(),
                quizRequest.getQuizType().name(),
                quizRequest.getLearningLevel().name(),
                quizRequest.getLanguage().name()
        );

        Cache quizListCache = cacheManager.getCache(QUIZ_LIST_CACHE_KEY);

        QuizListDto cachedDto = quizListCache != null ? quizListCache.get(quizListKey, QuizListDto.class) : null;
        if (cachedDto != null) {
            return CompletableFuture.completedFuture(cachedDto);
        }

        // 2. DB에서 값 조회
        Optional<QuizList> optionalQuiz = quizListRepository.findByMusicIdAndLevelAndQuizType(
                quizRequest.getMusicId(),
                quizRequest.getQuizType(),
                quizRequest.getLearningLevel()
        );

        if (optionalQuiz.isPresent()) {
            return CompletableFuture.completedFuture(new QuizListDto(optionalQuiz.get()));
        }

        return CompletableFuture.supplyAsync(() -> {
            // Redis에 저장
            String redisKey = "quizRequest:" + quizListKey;
            redisTemplate.opsForValue().set(redisKey, quizRequest, 3, TimeUnit.HOURS);

            // 예외 발생
            throw new CustomException(ErrorCode.QUIZ_NOT_FOUND);
        });
    }

    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<ListeningQuizDto> getListeningQuiz(QuizRequest quizRequest) {

        // 1. 캐시에서 값 조회
        String quizListKey = quizRequest.getMusicId() + "_" + quizRequest.getQuizType().name() + "_" + quizRequest.getLearningLevel().name() + "_" + quizRequest.getLanguage().name();
        Cache quizListCache = cacheManager.getCache(QUIZ_LIST_CACHE_KEY);

        ListeningQuizDto cachedDto = quizListCache != null ? quizListCache.get(quizListKey, ListeningQuizDto.class) : null;
        if (cachedDto != null) {
            return CompletableFuture.completedFuture(cachedDto);
        }

        // 2. DB에서 값 조회
        Optional<ListeningQuiz> optionalQuiz = listeningQuizRepository.findByMusicIdAndLevel(
                quizRequest.getMusicId(),
                quizRequest.getLearningLevel()
        );

        if (optionalQuiz.isPresent()) {
            return CompletableFuture.completedFuture(new ListeningQuizDto(optionalQuiz.get()));
        }

        // 3. QuizRequest 캐시에 추가 및 반환
        return CompletableFuture.supplyAsync(() -> {
            // Redis에 저장
            String redisKey = "listeningQuizRequest:" + quizListKey;
            redisTemplate.opsForValue().set(redisKey, quizRequest, 3, TimeUnit.HOURS);

            // 예외 발생
            throw new CustomException(ErrorCode.QUIZ_NOT_FOUND);
        });
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<QuizSubmitDto> submitQuiz(QuizSubmitRequest submitRequest, String memberId) {
        Member member = findMember(memberId);
        return CompletableFuture.supplyAsync(() -> quizSubmitService.submitQuiz(submitRequest, member));
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<ListeningSubmitDto> submitListeningQuiz(ListeningSubmitRequest submitRequest, String memberId) {
        Member member = findMember(memberId);
        return CompletableFuture.supplyAsync(() -> quizSubmitService.submitListeningQuiz(submitRequest, member));
    }

    private Member findMember(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
    }

    private double calCorrectRate(QuizSubmitRequest submitRequest, QuizList quizList) {
        List<Integer> submitAnswers = submitRequest.getAnswers();
        List<Quiz> quizzes = quizList.getQuizzes();
        int totalCorrectCount = 0;

        for (int i = 0; i < 4; i++) {
            Quiz quiz = quizzes.get(i);
            quiz.incrementSubmitCount();

            if (quiz.getAnswer() == submitAnswers.get(i)) {
                quiz.incrementCorrectCount();
                totalCorrectCount++;
            }
        }

        return totalCorrectCount * 100.0 / quizList.getQuizzes().size();
    }
}