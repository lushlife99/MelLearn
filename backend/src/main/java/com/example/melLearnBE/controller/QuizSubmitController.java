package com.example.melLearnBE.controller;

import com.example.melLearnBE.dto.model.ListeningSubmitDto;
import com.example.melLearnBE.dto.model.QuizSubmitDto;
import com.example.melLearnBE.dto.request.ListeningSubmitRequest;
import com.example.melLearnBE.dto.request.QuizSubmitRequest;
import com.example.melLearnBE.enums.QuizType;
import com.example.melLearnBE.service.QuizService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz/submit")
public class QuizSubmitController {

    private final QuizService quizService;

    @PostMapping({"/grammar", "/reading", "/vocabulary"})
    public QuizSubmitDto submit(@RequestBody QuizSubmitRequest quizSubmitRequest, HttpServletRequest request) {
        return quizService.submit(quizSubmitRequest, request);
    }

    @PostMapping("/listening")
    public ListeningSubmitDto listeningSubmit(@RequestBody ListeningSubmitRequest submitRequest, HttpServletRequest request) {
        return quizService.listeningSubmit(submitRequest, request);
    }

    @GetMapping
    public ResponseEntity getSubmitList(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
                                        @RequestParam(required = false, defaultValue = "createdAt", value = "sortBy") String sortBy,
                                        @RequestParam QuizType quizType,
                                        HttpServletRequest request) {

        return new ResponseEntity(quizService.getSubmitList(quizType, pageNo, sortBy, request), HttpStatus.OK);
    }

}