package com.mellearn.be.domain.quiz.choice.quiz.service;

import com.mellearn.be.domain.quiz.choice.submit.repository.QuizSubmitRepository;
import com.mellearn.be.domain.quiz.listening.quiz.dto.ListeningQuizDto;
import com.mellearn.be.domain.quiz.listening.quiz.entity.ListeningQuiz;
import com.mellearn.be.domain.quiz.listening.quiz.repository.ListeningQuizRepository;
import com.mellearn.be.domain.quiz.listening.submit.dto.ListeningSubmitDto;
import com.mellearn.be.domain.quiz.listening.submit.dto.request.ListeningSubmitRequest;
import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.repository.MemberRepository;
import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.entity.Quiz;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.repository.QuizListRepository;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitRequest;
import com.mellearn.be.domain.quiz.choice.submit.service.QuizSubmitService;
import com.mellearn.be.global.error.CustomException;
import com.mellearn.be.global.error.enums.ErrorCode;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final QuizSubmitService quizSubmitService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final QuizSubmitRepository quizSubmitRepository;
    private final QuizCreateService quizCreateService;
    private final QuizListRepository quizListRepository;
    private final ListeningQuizRepository listeningQuizRepository;
    private final MemberRepository memberRepository;

    public Page getSubmitList(QuizType quizType, int pageNo, String memberId) {
        Member member = findMember(memberId);

        if (quizType.equals(QuizType.LISTENING)) {
            return quizSubmitRepository.findListeningSubmitWithPaging(member.getId(), pageNo, 10);
        } else if (quizType.equals(QuizType.READING) || quizType.equals(QuizType.VOCABULARY) || quizType.equals(QuizType.GRAMMAR)) {
            return quizSubmitRepository.findSubmitWithPaging(member.getId(), quizType, pageNo, 10);
        } else if (quizType.equals(QuizType.SPEAKING)) {
            return quizSubmitRepository.findSpeakingSubmitWithPaging(member.getId(), pageNo, 10);
        }

        throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<QuizListDto> getQuizList(QuizRequest quizRequest, String memberId) {
        try {
            Member member = findMember(memberId);
            Optional<QuizList> optionalQuiz = fetchQuiz(quizRequest, member);
            String redisKey = getRedisKey(quizRequest, member);

            if (redisTemplate.hasKey(redisKey)) {
                throw new CustomException(ErrorCode.CREATING_OTHER_REQUEST);
            }

            if (optionalQuiz.isEmpty()) {
                redisTemplate.opsForValue().set(redisKey, "true", 1, TimeUnit.MINUTES);

                return CompletableFuture.supplyAsync(() -> {
                    try {
                        return quizCreateService.createChoiceQuiz(quizRequest, member);
                    } finally {
                        redisTemplate.delete(redisKey);
                    }
                });
            }
            return CompletableFuture.completedFuture(new QuizListDto(optionalQuiz.get()));
        } catch (Exception e) {
            log.error("Error in getQuizList: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<ListeningQuizDto> getListeningQuiz(QuizRequest quizRequest, String memberId) {
        try {
            Member member = findMember(memberId);
            Optional<ListeningQuiz> optionalQuiz = fetchListeningQuiz(quizRequest, member);
            String redisKey = getRedisKey(quizRequest, member);

            if (redisTemplate.hasKey(redisKey)) {
                throw new CustomException(ErrorCode.CREATING_OTHER_REQUEST);
            }

            if (optionalQuiz.isEmpty()) {
                redisTemplate.opsForValue().set(redisKey, "true", 1, TimeUnit.MINUTES);

                return CompletableFuture.supplyAsync(() -> {
                    try {
                        return quizCreateService.createListeningQuiz(quizRequest, member);
                    } finally {
                        redisTemplate.delete(redisKey);
                    }
                });
            }
            return CompletableFuture.completedFuture(new ListeningQuizDto(optionalQuiz.get()));
        } catch (Exception e) {
            log.error("Error in getListeningQuiz: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<QuizSubmitDto> submit(QuizSubmitRequest submitRequest, String memberId) {
        Member member = findMember(memberId);
        return CompletableFuture.supplyAsync(() -> quizSubmitService.submitQuiz(submitRequest, member));
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<ListeningSubmitDto> listeningSubmit(ListeningSubmitRequest submitRequest, String memberId) {
        Member member = findMember(memberId);
        return CompletableFuture.supplyAsync(() -> quizSubmitService.submitListeningQuiz(submitRequest, member));
    }

    private Optional<QuizList> fetchQuiz(QuizRequest quizRequest, Member member) {
        return quizListRepository.findByMusicIdAndQuizTypeAndLevel(
                quizRequest.getMusicId(),
                quizRequest.getQuizType(),
                member.getLevel()
        );
    }

    private Optional<ListeningQuiz> fetchListeningQuiz(QuizRequest quizRequest, Member member) {
        return listeningQuizRepository.findByMusicIdAndLevel(
                quizRequest.getMusicId(),
                member.getLevel()
        );
    }

    private String getRedisKey(QuizRequest quizRequest, Member member) {
        return String.format("%s:%s:%s:%s",
                quizRequest.getMusicId(),
                quizRequest.getQuizType(),
                member.getLevel(),
                member.getMemberId()
        );
    }

    private Member findMember(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
    }

    public QuizSubmitDto submitQuiz(QuizSubmitRequest submitRequest, String memberId) {
        Member member = findMember(memberId);
        return quizSubmitService.submitQuiz(submitRequest, member);
    }

    public ListeningSubmitDto submitListeningQuiz(ListeningSubmitRequest submitRequest, String memberId) {
        Member member = findMember(memberId);
        return quizSubmitService.submitListeningQuiz(submitRequest, member);
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