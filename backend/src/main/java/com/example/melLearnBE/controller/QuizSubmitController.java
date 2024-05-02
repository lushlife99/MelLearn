package com.example.melLearnBE.controller;

import com.example.melLearnBE.dto.model.ListeningSubmitDto;
import com.example.melLearnBE.dto.model.QuizSubmitDto;
import com.example.melLearnBE.dto.request.ListeningSubmitRequest;
import com.example.melLearnBE.dto.request.QuizSubmitRequest;
import com.example.melLearnBE.service.QuizService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
