package com.mellearn.be.domain.quiz.choice.quiz.service.async;

import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.service.QuizService;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitRequest;
import com.mellearn.be.domain.quiz.listening.quiz.dto.ListeningQuizDto;
import com.mellearn.be.domain.quiz.listening.submit.dto.ListeningSubmitDto;
import com.mellearn.be.domain.quiz.listening.submit.dto.request.ListeningSubmitRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizAsyncService {

    private final QuizService quizService;

    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<QuizListDto> getQuizList(QuizRequest request) {
        return CompletableFuture.completedFuture(quizService.getQuizList(request));
    }

    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<ListeningQuizDto> getListeningQuiz(QuizRequest request) {
        return CompletableFuture.completedFuture(quizService.getListeningQuiz(request));
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<QuizSubmitDto> submitQuiz(QuizSubmitRequest submitRequest, String memberId) {
        return CompletableFuture.supplyAsync(() -> quizService.submitQuiz(submitRequest, memberId));
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<ListeningSubmitDto> submitListeningQuiz(ListeningSubmitRequest submitRequest, String memberId) {
        return CompletableFuture.supplyAsync(() -> quizService.submitListeningQuiz(submitRequest, memberId));
    }
}