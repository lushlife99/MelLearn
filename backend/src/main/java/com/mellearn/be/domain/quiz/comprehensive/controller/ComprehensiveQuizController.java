package com.mellearn.be.domain.quiz.comprehensive.controller;

import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.comprehensive.dto.ComprehensiveQuizDto;
import com.mellearn.be.domain.quiz.comprehensive.dto.ComprehensiveQuizSubmitDto;
import com.mellearn.be.domain.quiz.comprehensive.dto.ComprehensiveQuizSubmitRequest;
import com.mellearn.be.domain.quiz.comprehensive.service.ComprehensiveQuizService;
import com.mellearn.be.global.auth.jwt.service.JwtTokenProvider;
import com.mellearn.be.global.error.CustomException;
import com.mellearn.be.global.error.enums.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @Operation(summary = "모의고사 문제 조회", description = "모의고사 문제 조회")
    public ComprehensiveQuizDto getComprehensiveQuiz(@RequestBody QuizRequest quizRequest, Authentication authentication) throws ExecutionException, InterruptedException {
        try {
            return comprehensiveQuizService.get(quizRequest, authentication.getName());
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_QUIZ);
        } catch (ExecutionException e) {
            throw new CustomException(ErrorCode.CREATING_OTHER_REQUEST);
        }
    }

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "모의고사 답안지 제출", description = "모의고사 답안지 제출")
    public ComprehensiveQuizSubmitDto submit(@RequestPart("submitRequest") ComprehensiveQuizSubmitRequest submitRequest,
                                             @RequestPart("speakingSubmitFile") MultipartFile speakingSubmitFile,
                                             Authentication authentication) throws ExecutionException, InterruptedException {
   
        return comprehensiveQuizService.submit(submitRequest, speakingSubmitFile, authentication.getName());
    }
}
