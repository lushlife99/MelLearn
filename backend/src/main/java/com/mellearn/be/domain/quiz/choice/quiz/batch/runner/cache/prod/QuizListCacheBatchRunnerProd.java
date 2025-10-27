package com.mellearn.be.domain.quiz.choice.quiz.batch.runner.cache.prod;

import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.music.chart.enums.ChartType;
import com.mellearn.be.domain.quiz.choice.quiz.batch.service.QuizListCacheBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class QuizListCacheBatchRunnerProd {

    private final QuizListCacheBatchService batchService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        runCacheBatch();
    }

    // 매일 00시 01분에 실행
    @Scheduled(cron = "0 1 0 * * *")
    public void runCacheBatch() {
        batchService.cachePopularMusicQuizLists(ChartType.daily, Language.ENGLISH);
    }
}