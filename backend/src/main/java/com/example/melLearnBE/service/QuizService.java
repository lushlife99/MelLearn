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
import com.example.melLearnBE.jwt.JwtTokenProvider;
import com.example.melLearnBE.model.*;
import com.example.melLearnBE.repository.ListeningQuizRepository;
import com.example.melLearnBE.repository.QuizListRepository;
import com.example.melLearnBE.repository.querydsl.SubmitJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final SubmitJpaRepository submitJpaRepository;
    private final QuizCreationService quizCreationService;
    private final QuizListRepository quizListRepository;
    private final ListeningQuizRepository listeningQuizRepository;

    public Page getSubmitList(QuizType quizType, int pageNo, HttpServletRequest request) {
        Member member = jwtTokenProvider.getMember(request)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));

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
    public CompletableFuture<QuizListDto> getQuizList(QuizRequest quizRequest, HttpServletRequest request) {
        try {
            Member member = jwtTokenProvider.getMember(request)
                    .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
            Optional optionalQuiz = fetchQuiz(quizRequest, member);
            String redisKey = getRedisKey(quizRequest, member);

            if (optionalQuiz.isEmpty()) {
                if (redisTemplate.hasKey(redisKey)) {
                    throw new CustomException(ErrorCode.CREATING_OTHER_REQUEST);
                } else {
                    redisTemplate.opsForValue().set(redisKey, "true", 1, TimeUnit.MINUTES);
                    CompletableFuture<QuizListDto> quizListFuture = quizCreationService.createQuizList(quizRequest, member);
                    quizListFuture.whenComplete((result, exception) -> {
                        redisTemplate.delete(redisKey);
                        if (exception != null) {
                            log.error("Error creating quiz list: {}", exception.getMessage());
                        }
                    });
                    return quizListFuture;
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
    public CompletableFuture<ListeningQuizDto> getListeningQuiz(QuizRequest quizRequest, HttpServletRequest request) {
        try {
            Member member = jwtTokenProvider.getMember(request)
                    .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
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
    public CompletableFuture<QuizSubmitDto> submit(QuizSubmitRequest submitRequest, HttpServletRequest request) {
        try {
            Member member = jwtTokenProvider.getMember(request)
                    .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
            return CompletableFuture.completedFuture(quizSubmitService.submitQuiz(submitRequest, member));
        } catch (Exception e) {
            log.error("Error in submit: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<ListeningSubmitDto> listeningSubmit(ListeningSubmitRequest submitRequest, HttpServletRequest request) {
        try {
            Member member = jwtTokenProvider.getMember(request)
                    .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
            return CompletableFuture.completedFuture(quizSubmitService.submitListeningQuiz(submitRequest, member));
        } catch (Exception e) {
            log.error("Error in listeningSubmit: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public Optional fetchQuiz(QuizRequest quizRequest, Member member) {
        if (quizRequest.getQuizType().equals(QuizType.LISTENING)) {
            return listeningQuizRepository.findByMusicIdAndLevel(quizRequest.getMusicId(), member.getLevel());
        } else {
            return quizListRepository.findByMusicIdAndQuizTypeAndLevel(quizRequest.getMusicId(), quizRequest.getQuizType(), member.getLevel());
        }
    }

    private String getRedisKey(QuizRequest quizRequest, Member member) {
        return String.join(":",
                quizRequest.getMusicId(),
                quizRequest.getQuizType().toString(),
                String.valueOf(member.getLevel())
        );
    }

    private double calCorrectRate(QuizSubmitRequest submitRequest, QuizList quizList) {
        List<Integer> submitAnswers = submitRequest.getAnswers();
        List<Quiz> quizzes = quizList.getQuizzes();
        int totalCorrectCount = 0;
        for (int i = 0; i < 4; i++) {
            Quiz quiz = quizzes.get(i);
            quiz.setSubmitCount(quiz.getSubmitCount() + 1);
            if (quiz.getAnswer() == submitAnswers.get(i)) {
                quiz.setCorrectCount(quiz.getCorrectCount() + 1);
                totalCorrectCount++;
            }
        }

        return totalCorrectCount * 100 / quizList.getQuizzes().size();
    }

}