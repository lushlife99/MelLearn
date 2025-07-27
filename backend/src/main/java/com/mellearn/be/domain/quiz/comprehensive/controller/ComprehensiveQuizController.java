package com.mellearn.be.domain.quiz.comprehensive.controller;

import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.comprehensive.dto.ComprehensiveQuizDto;
import com.mellearn.be.domain.quiz.comprehensive.dto.ComprehensiveQuizSubmitDto;
import com.mellearn.be.domain.quiz.comprehensive.dto.ComprehensiveQuizSubmitRequest;
import com.mellearn.be.domain.quiz.comprehensive.service.ComprehensiveQuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@Tag(name = "ComprehensiveQuiz")
@RequestMapping("/api/comprehensive-quiz")
public class ComprehensiveQuizController {

    private final ComprehensiveQuizService comprehensiveQuizService;

    @PostMapping
    @Operation(summary = "모의고사 문제 조회", description = "모의고사 문제 조회")
    public ComprehensiveQuizDto getComprehensiveQuiz(@RequestBody QuizRequest quizRequest, Authentication authentication) throws InterruptedException, ExecutionException {

        return comprehensiveQuizService.get(quizRequest, authentication.getName());
    }

    /**
     * 25.07.26 ~
     * Spotify 계정이 없는 관계로 현재 Speaking 서비스 불가
     * 모의고사 채점 서비스에서 Speaking 제공 X
     */

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "모의고사 답안지 제출", description = "모의고사 답안지 제출")
    public ComprehensiveQuizSubmitDto submit(@RequestPart("submitRequest") ComprehensiveQuizSubmitRequest submitRequest,
                                             @RequestPart(value = "speakingSubmitFile", required = false) MultipartFile speakingSubmitFile,
                                             Authentication authentication) throws ExecutionException, InterruptedException {

        return comprehensiveQuizService.submit(submitRequest, authentication.getName());
    }
}
