package com.mellearn.be.domain.quiz.choice.quiz.batch.service;

import com.mellearn.be.domain.quiz.choice.quiz.service.QuizCreateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizCreateBatchService {

    private static final String QUIZ_REQUEST_KEY = "quizRequest";
    private static final String LISTENING_QUIZ_REQUEST_KEY = "listeningQuizRequest";

    private final QuizCreateService quizCreateService;
    private final RedisTemplate<String, Object> redisTemplate;

    public void createQuizList() {
        createChoiceQuizList();
        createListeningQuizList();
    }

    private void createChoiceQuizList() {
//        quizCreateService.createChoiceQuiz();
    }

    private void createListeningQuizList() {

    }
}
