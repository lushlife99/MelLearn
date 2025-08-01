package com.mellearn.be.domain.quiz.choice.quiz.controller;

import com.mellearn.be.domain.quiz.listening.quiz.dto.ListeningQuizDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.service.QuizService;
import com.mellearn.be.global.error.CustomException;
import com.mellearn.be.global.error.enums.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Quiz")
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    @PostMapping({"/reading", "/vocabulary", "/grammar"})
    @Operation(summary = "퀴즈 조회", description = "퀴즈 조회")
    public QuizListDto getQuizList(@RequestBody QuizRequest quizRequest, Authentication authentication) throws InterruptedException, ExecutionException {
        return quizService.getQuizList(quizRequest, authentication.getName()).get();
    }

    @PostMapping("/listening")
    @Operation(summary = "퀴즈 조회", description = "퀴즈 조회")
    public ListeningQuizDto getListeningQuiz(@RequestBody QuizRequest quizRequest, Authentication authentication) throws InterruptedException, ExecutionException {
        return quizService.getListeningQuiz(quizRequest, authentication.getName()).get();
    }

}
