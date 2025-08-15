package com.mellearn.be.domain.quiz.choice.submit.controller;

import com.mellearn.be.domain.quiz.listening.submit.dto.ListeningSubmitDto;
import com.mellearn.be.domain.quiz.listening.submit.dto.request.ListeningSubmitRequest;
import com.mellearn.be.domain.quiz.choice.quiz.service.QuizService;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitRequest;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;


@RestController
@RequiredArgsConstructor
@Tag(name = "Quiz Submit")
@RequestMapping("/api/quiz/submit")
public class QuizSubmitController {

    private final QuizService quizService;

    @PostMapping({"/grammar", "/reading", "/vocabulary"})
    @Operation(summary = "퀴즈 답안지 제출", description = "퀴즈 답안지 제출")
    public QuizSubmitDto submit(@RequestBody QuizSubmitRequest quizSubmitRequest, Authentication authentication) throws ExecutionException, InterruptedException {
        return quizService.submitQuiz(quizSubmitRequest, authentication.getName()).get();
    }

    @PostMapping("/listening")
    @Operation(summary = "퀴즈 답안지 제출", description = "퀴즈 답안지 제출")
    public ListeningSubmitDto listeningSubmit(@RequestBody ListeningSubmitRequest submitRequest, Authentication authentication) throws ExecutionException, InterruptedException {
        return quizService.submitListeningQuiz(submitRequest, authentication.getName()).get();
    }

    @GetMapping
    @Operation(summary = "제출 답안지, 채점 조회", description = "제출 답안지, 채점 조회")
    public ResponseEntity getSubmitList(@RequestParam(required = false, value = "lastSeenId") Long lastSeenId,
                                        @RequestParam QuizType quizType,
                                        Authentication authentication) {
        List submitList = quizService.getSubmitList(quizType, lastSeenId, authentication.getName());
        return new ResponseEntity(submitList, HttpStatus.OK);
    }

}