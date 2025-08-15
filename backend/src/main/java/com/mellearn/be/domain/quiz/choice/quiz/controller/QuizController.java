package com.mellearn.be.domain.quiz.choice.quiz.controller;

import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.service.QuizService;
import com.mellearn.be.domain.quiz.listening.quiz.dto.ListeningQuizDto;
import com.mellearn.be.global.auth.jwt.service.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Quiz")
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping({"/reading", "/vocabulary", "/grammar"})
    @Operation(summary = "퀴즈 조회", description = "선택형 퀴즈 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "퀴즈 조회 성공"),
            @ApiResponse(responseCode = "404", description = "퀴즈가 존재하지 않음. 퀴즈 생성 큐에 추가."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public QuizListDto getQuizList(QuizRequest quizRequest) throws InterruptedException, ExecutionException {
        return quizService.getQuizList(quizRequest).get();
    }
    @PostMapping("/listening")
    @Operation(summary = "퀴즈 조회", description = "듣기 퀴즈 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "퀴즈 조회 성공"),
            @ApiResponse(responseCode = "404", description = "퀴즈가 존재하지 않음. 퀴즈 생성 큐에 추가."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ListeningQuizDto getListeningQuiz(QuizRequest quizRequest) throws InterruptedException, ExecutionException {
        return quizService.getListeningQuiz(quizRequest).get();
    }

}
