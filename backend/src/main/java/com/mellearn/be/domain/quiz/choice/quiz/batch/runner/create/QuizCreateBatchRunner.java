package com.mellearn.be.domain.quiz.choice.quiz.batch.runner.create;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mellearn.be.domain.quiz.choice.quiz.batch.service.QuizCreateBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizCreateBatchRunner {

    private final QuizCreateBatchService quizCreateBatchService;

    @Scheduled(cron = "0 0 * * * ?")
//    @Scheduled(cron = "0 0/1 * * * ?") // 5분마다 실행. 테스트 용도
    public void runCreateQuizzes() {
        quizCreateBatchService.createQuizListBatch();
    }

    @Scheduled(cron = "0 0 0/6 * * ?") // 매 6시간마다 실행 (0시, 6시, 12시, 18시)
//    @Scheduled(cron = "0 0/1 * * * ?") // 5분마다 실행. 테스트 용도
    public void saveBatchResults() throws JsonProcessingException {
        quizCreateBatchService.processQuizCreateBatchResult();
    }

}