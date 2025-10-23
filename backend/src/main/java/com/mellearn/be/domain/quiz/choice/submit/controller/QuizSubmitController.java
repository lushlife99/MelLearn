package com.mellearn.be.domain.quiz.choice.submit.controller;

import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.mellearn.be.domain.quiz.choice.quiz.service.QuizService;
import com.mellearn.be.domain.quiz.choice.submit.dto.MusicQuizSubmit;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitRequest;
import com.mellearn.be.domain.quiz.listening.submit.dto.ListeningSubmitDto;
import com.mellearn.be.domain.quiz.listening.submit.dto.request.ListeningSubmitRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Tag(name = "Quiz Submit")
@RequestMapping("/api/quiz/submit")
public class QuizSubmitController {

    private final QuizService quizService;

    @PostMapping({"/grammar", "/reading", "/vocabulary"})
    @Operation(summary = "퀴즈 답안지 제출", description = "퀴즈 답안지 제출")
    public QuizSubmitDto submit(@RequestBody QuizSubmitRequest quizSubmitRequest, Authentication authentication) {
        return quizService.submitQuiz(quizSubmitRequest, authentication.getName());
    }

    @PostMapping("/listening")
    @Operation(summary = "퀴즈 답안지 제출", description = "퀴즈 답안지 제출")
    public ListeningSubmitDto listeningSubmit(@RequestBody ListeningSubmitRequest submitRequest, Authentication authentication) {
        return quizService.submitListeningQuiz(submitRequest, authentication.getName());
    }

    @GetMapping
    @Operation(summary = "전체 제출 내역 페이지네이션", description = "내가 설정한 난이도, 최신순 정렬하여 10개씩 페이징")
    public List<MusicQuizSubmit> getSubmitList(@RequestParam LearningLevel level,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastSeen) {

        return quizService.getSubmitPage(level, lastSeen);
    }

    @GetMapping("/history")
    @Operation(summary = "제출 답안지, 채점 조회", description = "제출 답안지, 채점 조회")
    public ResponseEntity getHistory(@RequestParam(required = false, value = "lastSeenId") Long lastSeenId,
                                        @RequestParam QuizType quizType,
                                        Authentication authentication) {
        List submitList = quizService.getHistory(quizType, lastSeenId, authentication.getName());
        return new ResponseEntity(submitList, HttpStatus.OK);
    }

}