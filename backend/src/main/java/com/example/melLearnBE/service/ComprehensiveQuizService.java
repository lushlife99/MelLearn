package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.model.ListeningQuizDto;
import com.example.melLearnBE.dto.request.QuizRequest;
import com.example.melLearnBE.dto.response.ComprehensiveQuizDto;
import com.example.melLearnBE.enums.QuizType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComprehensiveQuizService {

    private final QuizService quizService;
    private final SpeakingService speakingService;

    public ComprehensiveQuizDto getComprehensiveQuiz(QuizRequest quizRequest, HttpServletRequest request) {

        CompletableFuture<ListeningQuizDto> listeningQuiz = quizService.getListeningQuiz(QuizRequest.builder()
                .quizType(QuizType.LISTENING)
                .lyric(quizRequest.getLyric())
                .musicId(quizRequest.getMusicId())
                .build(), request);


        return null;
    }
}
