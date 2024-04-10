package com.example.melLearnBE.controller;

import com.example.melLearnBE.dto.model.ListeningQuizDto;
import com.example.melLearnBE.dto.model.QuizListDto;
import com.example.melLearnBE.dto.model.QuizSubmitDto;
import com.example.melLearnBE.dto.request.QuizRequest;
import com.example.melLearnBE.dto.request.QuizSubmitRequest;
import com.example.melLearnBE.model.ListeningQuiz;
import com.example.melLearnBE.service.QuizService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    @PostMapping({"/reading", "/vocabulary", "/grammar"})
    public QuizListDto getQuizList(@RequestBody QuizRequest quizRequest, HttpServletRequest request) {
        return quizService.getQuizList(quizRequest, request);
    }


    @PostMapping("/listening")
    public ListeningQuizDto getListeningQuiz(@RequestBody QuizRequest quizRequest, HttpServletRequest request) {
        return quizService.getListeningQuiz(quizRequest, request);
    }

}
