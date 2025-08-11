package com.mellearn.be.domain.quiz.choice.quiz.batch.runner;

import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.batch.service.QuizListCacheBatchService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class QuizListCacheBatchRunner {

    private final QuizListCacheBatchService batchService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        runCacheBatch();
    }

    // 매일 00시 01분에 실행
    @Scheduled(cron = "0 1 0 * * *")
    public void runCacheBatch() {
        Map<String, String> popularMusicMap = fetchPopularMusicMap();

        batchService.cachePopularMusicQuizLists(popularMusicMap, Language.ENGLISH);
    }

    /**
     * 인기 음악 ID와 가사 등 추가 정보를 담은 Map 조회 또는 외부 API 호출
     *
     * 현재는 Spotify API 만료됐기 때문에 const 하게 관리
     */

    private Map<String, String> fetchPopularMusicMap() {

        return Map.of(
                "b9798c9e-71c0-11f0-a8e6-0adc305930e5", "lyric1"
        );
    }
}