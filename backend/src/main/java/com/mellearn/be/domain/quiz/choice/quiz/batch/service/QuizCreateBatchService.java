package com.mellearn.be.domain.quiz.choice.quiz.batch.service;

import com.mellearn.be.domain.quiz.choice.quiz.service.QuizCreateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizCreateBatchService {

    private final QuizCreateService quizCreateService;
}
