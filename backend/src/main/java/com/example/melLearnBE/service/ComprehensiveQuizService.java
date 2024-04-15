package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.model.ListeningQuizDto;
import com.example.melLearnBE.dto.model.QuizListDto;
import com.example.melLearnBE.dto.request.QuizRequest;
import com.example.melLearnBE.dto.response.ComprehensiveQuizDto;
import com.example.melLearnBE.enums.QuizType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComprehensiveQuizService {

    private final QuizService quizService;
    private final SpeakingService speakingService;

    public ComprehensiveQuizDto getComprehensiveQuiz(QuizRequest quizRequest, HttpServletRequest request) throws InterruptedException, ExecutionException {
        CompletableFuture<ListeningQuizDto> listeningQuiz = quizService.getListeningQuiz(QuizRequest.builder()
                .quizType(QuizType.LISTENING)
                .lyric(quizRequest.getLyric())
                .musicId(quizRequest.getMusicId())
                .build(), request);

        CompletableFuture<QuizListDto> readingQuiz = quizService.getQuizList(QuizRequest.builder()
                .quizType(QuizType.READING)
                .lyric(quizRequest.getLyric())
                .musicId(quizRequest.getMusicId())
                .build(), request);

        CompletableFuture<QuizListDto> vocaQuiz = quizService.getQuizList(QuizRequest.builder()
                .quizType(QuizType.VOCABULARY)
                .lyric(quizRequest.getLyric())
                .musicId(quizRequest.getMusicId())
                .build(), request);

        CompletableFuture<QuizListDto> grammarQuiz = quizService.getQuizList(QuizRequest.builder()
                .quizType(QuizType.GRAMMAR)
                .lyric(quizRequest.getLyric())
                .musicId(quizRequest.getMusicId())
                .build(), request);

        CompletableFuture.allOf(listeningQuiz, readingQuiz, vocaQuiz, grammarQuiz).join();

        ListeningQuizDto listeningResult = listeningQuiz.get();
        QuizListDto readingResult = readingQuiz.get();
        QuizListDto vocaResult = vocaQuiz.get();
        QuizListDto grammarResult = grammarQuiz.get();
        ComprehensiveQuizDto comprehensiveQuizDto = new ComprehensiveQuizDto(grammarResult, vocaResult, readingResult, listeningResult);
        System.out.println("comprehensiveQuizDto = " + comprehensiveQuizDto);
        return comprehensiveQuizDto;
    }

}
