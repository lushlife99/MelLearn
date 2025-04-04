package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.model.ListeningQuizDto;
import com.example.melLearnBE.dto.model.ListeningSubmitDto;
import com.example.melLearnBE.dto.model.QuizListDto;
import com.example.melLearnBE.dto.model.QuizSubmitDto;
import com.example.melLearnBE.dto.request.ListeningSubmitRequest;
import com.example.melLearnBE.dto.request.QuizRequest;
import com.example.melLearnBE.dto.request.QuizSubmitRequest;
import com.example.melLearnBE.enums.QuizType;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.model.*;
import com.example.melLearnBE.repository.ListeningQuizRepository;
import com.example.melLearnBE.repository.QuizListRepository;
import com.example.melLearnBE.repository.querydsl.SubmitJpaRepository;
import com.example.melLearnBE.repository.MemberRepository;
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
    private final SubmitJpaRepository submitJpaRepository;
    private final QuizCreationService quizCreationService;
    private final QuizListRepository quizListRepository;
    private final ListeningQuizRepository listeningQuizRepository;
    private final MemberRepository memberRepository;

    public Page getSubmitList(QuizType quizType, int pageNo, String memberId) {
        Member member = findMember(memberId);

        if (quizType.equals(QuizType.LISTENING)) {
            return submitJpaRepository.findListeningSubmitWithPaging(member.getId(), pageNo, 10);
        } else if (quizType.equals(QuizType.READING) || quizType.equals(QuizType.VOCABULARY) || quizType.equals(QuizType.GRAMMAR)) {
            return submitJpaRepository.findSubmitWithPaging(member.getId(), quizType, pageNo, 10);
        } else if (quizType.equals(QuizType.SPEAKING)) {
            return submitJpaRepository.findSpeakingSubmitWithPaging(member.getId(), pageNo, 10);
        }

        throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<QuizListDto> getQuizList(QuizRequest quizRequest, String memberId) {
        try {
            Member member = findMember(memberId);
            Optional optionalQuiz = fetchQuiz(quizRequest, member);
            String redisKey = getRedisKey(quizRequest, member);

            if (optionalQuiz.isEmpty()) {
                if (redisTemplate.hasKey(redisKey)) {
                    throw new CustomException(ErrorCode.CREATING_OTHER_REQUEST);
                } else {
                    redisTemplate.opsForValue().set(redisKey, "true", 1, TimeUnit.MINUTES);
                    CompletableFuture<QuizListDto> quizList = quizCreationService.createQuizList(quizRequest, member);
                    quizList.whenComplete((result, exception) -> {
                        redisTemplate.delete(redisKey);
                        if (exception != null) {
                            log.error("Error creating quiz list: {}", exception.getMessage());
                        }
                    });
                    return quizList;
                }
            }
            return CompletableFuture.completedFuture(new QuizListDto((QuizList) optionalQuiz.get()));
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
            Optional optionalQuiz = fetchQuiz(quizRequest, member);
            String redisKey = getRedisKey(quizRequest, member);

            if (optionalQuiz.isEmpty()) {
                if (redisTemplate.hasKey(redisKey)) {
                    throw new CustomException(ErrorCode.CREATING_OTHER_REQUEST);
                } else {
                    redisTemplate.opsForValue().set(redisKey, "true", 1, TimeUnit.MINUTES);
                    CompletableFuture<ListeningQuizDto> listeningQuiz = quizCreationService.createListeningQuiz(quizRequest, member);
                    listeningQuiz.whenComplete((result, exception) -> {
                        redisTemplate.delete(redisKey);
                        if (exception != null) {
                            log.error("Error creating listening quiz: {}", exception.getMessage());
                        }
                    });
                    return listeningQuiz;
                }
            }
            return CompletableFuture.completedFuture(new ListeningQuizDto((ListeningQuiz) optionalQuiz.get()));
        } catch (Exception e) {
            log.error("Error in getListeningQuiz: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<QuizSubmitDto> submit(QuizSubmitRequest submitRequest, String memberId) {
        try {
            Member member = findMember(memberId);
            return CompletableFuture.completedFuture(quizSubmitService.submitQuiz(submitRequest, member));
        } catch (Exception e) {
            log.error("Error in submit: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<ListeningSubmitDto> listeningSubmit(ListeningSubmitRequest submitRequest, String memberId) {
        try {
            Member member = findMember(memberId);
            return CompletableFuture.completedFuture(quizSubmitService.submitListeningQuiz(submitRequest, member));
        } catch (Exception e) {
            log.error("Error in listeningSubmit: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    private Member findMember(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> {
                    log.error("Member not found with id: {}", memberId);
                    return new CustomException(ErrorCode.BAD_REQUEST);
                });
    }

    private Optional fetchQuiz(QuizRequest quizRequest, Member member) {
        if (quizRequest.getQuizType().equals(QuizType.LISTENING)) {
            return listeningQuizRepository.findByMusicIdAndLevel(quizRequest.getMusicId(), member.getLevel());
        } else {
            return quizListRepository.findByMusicIdAndQuizTypeAndLevel(quizRequest.getMusicId(), quizRequest.getQuizType(), member.getLevel());
        }
    }

    private String getRedisKey(QuizRequest quizRequest, Member member) {
        return quizRequest.getQuizType() + ":" + quizRequest.getMusicId() + ":" + member.getLevel();
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