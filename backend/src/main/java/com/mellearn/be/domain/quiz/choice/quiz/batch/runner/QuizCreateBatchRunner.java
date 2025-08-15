package com.mellearn.be.domain.quiz.choice.quiz.batch.runner;

import com.mellearn.be.domain.quiz.choice.quiz.batch.service.QuizCreateBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizCreateBatchRunner {

    private final QuizCreateBatchService quizCreateBatchService;

//    @Scheduled(cron = "0 0 * * * ?")
    @Scheduled(cron = "0 0/5 * * * ?") // 5분마다 실행. 테스트 용도
    public void runCreateQuizzes() {
        quizCreateBatchService.createQuizList();
    }

}
