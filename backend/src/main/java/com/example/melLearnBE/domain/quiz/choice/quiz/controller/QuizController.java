package com.example.melLearnBE.domain.quiz.choice.quiz.controller;

import com.example.melLearnBE.domain.listening.quiz.dto.ListeningQuizDto;
import com.example.melLearnBE.domain.quiz.choice.quiz.dto.QuizListDto;
import com.example.melLearnBE.domain.quiz.choice.quiz.dto.QuizRequest;
import com.example.melLearnBE.domain.quiz.choice.quiz.service.QuizService;
import com.example.melLearnBE.global.error.CustomException;
import com.example.melLearnBE.global.error.enums.ErrorCode;
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
    public QuizListDto getQuizList(@RequestBody QuizRequest quizRequest, Authentication authentication) throws InterruptedException {
        try {
            return quizService.getQuizList(quizRequest, authentication.getName()).get();
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_QUIZ);
        } catch (ExecutionException e) {
            throw new CustomException(ErrorCode.CREATING_OTHER_REQUEST);
        }
    }

    @PostMapping("/listening")
    @Operation(summary = "퀴즈 조회", description = "퀴즈 조회")
    public ListeningQuizDto getListeningQuiz(@RequestBody QuizRequest quizRequest, Authentication authentication) throws InterruptedException {
        try {
            return quizService.getListeningQuiz(quizRequest, authentication.getName()).get();
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_QUIZ);
        } catch (ExecutionException e) {
            throw new CustomException(ErrorCode.CREATING_OTHER_REQUEST);
        }
    }

}
