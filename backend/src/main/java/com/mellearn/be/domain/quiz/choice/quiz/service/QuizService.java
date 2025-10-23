package com.mellearn.be.domain.quiz.choice.quiz.service;

import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.member.repository.MemberRepository;
import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.mellearn.be.domain.quiz.choice.quiz.repository.QuizListRepository;
import com.mellearn.be.domain.quiz.choice.submit.dto.MusicQuizSubmit;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitRequest;
import com.mellearn.be.domain.quiz.choice.submit.repository.QuizSubmitRepository;
import com.mellearn.be.domain.quiz.choice.submit.service.QuizSubmitService;
import com.mellearn.be.domain.quiz.listening.quiz.dto.ListeningQuizDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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

    public QuizListDto getQuizList(QuizRequest quizRequest) {
        String key = String.join("_",
                quizRequest.getMusicId(),
                quizRequest.getQuizType().name(),
                quizRequest.getLearningLevel().name(),
                quizRequest.getLanguage().name());

        Cache cache = cacheManager.getCache(QUIZ_LIST_CACHE_KEY);
        QuizListDto cached = cache != null ? cache.get(key, QuizListDto.class) : null;
        if (cached != null) return cached;

        return quizListRepository.findByMusicIdAndLevelAndQuizType(
                        quizRequest.getMusicId(),
                        quizRequest.getQuizType(),
                        quizRequest.getLearningLevel())
                .map(QuizListDto::new)
                .orElseThrow(() -> {
                    redisTemplate.opsForValue().set("quizRequest:" + key, quizRequest, 3, TimeUnit.HOURS);
                    throw new CustomException(ErrorCode.QUIZ_NOT_FOUND);
                });
    }

    @Transactional(readOnly = true)
    public ListeningQuizDto getListeningQuiz(QuizRequest quizRequest) {
        String key = quizRequest.getMusicId() + "_" + quizRequest.getQuizType().name() + "_" +
                quizRequest.getLearningLevel().name() + "_" + quizRequest.getLanguage().name();

        Cache cache = cacheManager.getCache(QUIZ_LIST_CACHE_KEY);
        ListeningQuizDto cached = cache != null ? cache.get(key, ListeningQuizDto.class) : null;
        if (cached != null) return cached;

        return listeningQuizRepository.findByMusicIdAndLevel(
                        quizRequest.getMusicId(),
                        quizRequest.getLearningLevel())
                .map(ListeningQuizDto::new)
                .orElseThrow(() -> {
                    redisTemplate.opsForValue().set("quizRequest:" + key, quizRequest, 3, TimeUnit.HOURS);
                    throw new CustomException(ErrorCode.QUIZ_NOT_FOUND);
                });
    }

    @Transactional
    public QuizSubmitDto submitQuiz(QuizSubmitRequest submitRequest, String memberId) {
        Member member = findMember(memberId);
        return quizSubmitService.submitQuiz(submitRequest, member);
    }

    @Transactional
    public ListeningSubmitDto submitListeningQuiz(ListeningSubmitRequest submitRequest, String memberId) {
        Member member = findMember(memberId);
        return quizSubmitService.submitListeningQuiz(submitRequest, member);
    }

    @Transactional(readOnly = true)
    public List<?> getHistory(QuizType quizType, Long lastSeenId, String memberId) {
        Member member = findMember(memberId);
        if (quizType.equals(QuizType.LISTENING)) {
            return quizSubmitRepository.findListeningHistoryPage(member.getId(), lastSeenId, PAGE_SIZE);
        } else if (quizType.equals(QuizType.SPEAKING)) {
            return quizSubmitRepository.findSpeakingSubmitWithPaging(member.getId(), lastSeenId, PAGE_SIZE);
        } else {
            return quizSubmitRepository.findHistoryPage(member.getId(), quizType, lastSeenId, PAGE_SIZE);
        }
    }

    @Transactional(readOnly = true)
    public List<MusicQuizSubmit> getSubmitPage(LearningLevel level, LocalDateTime lastSeen) {
        return quizSubmitRepository.findSubmitPage(level, lastSeen);
    }

    private Member findMember(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
    }
}
