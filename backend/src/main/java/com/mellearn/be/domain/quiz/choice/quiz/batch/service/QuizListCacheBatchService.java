package com.mellearn.be.domain.quiz.choice.quiz.batch.service;

import com.mellearn.be.api.feign.spotify.SpotifyClient;
import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.music.chart.dto.ChartDto;
import com.mellearn.be.domain.music.chart.enums.ChartType;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.mellearn.be.domain.quiz.choice.quiz.repository.QuizListRepository;
import com.mellearn.be.domain.quiz.choice.quiz.service.QuizService;
import com.mellearn.be.domain.quiz.listening.quiz.entity.ListeningQuiz;
import com.mellearn.be.domain.quiz.listening.quiz.repository.ListeningQuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizListCacheBatchService {

    private final QuizService quizService;
    private final SpotifyClient spotifyClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final QuizListRepository quizListRepository;
    private final ListeningQuizRepository listeningQuizRepository;

    private static final Map<ChartType, Duration> TTL_MAP = Map.of(
            ChartType.monthly, Duration.ofDays(365),
            ChartType.weekly, Duration.ofDays(180),
            ChartType.daily, Duration.ofDays(30)
    );

    public void cachePopularMusicQuizLists(Map<String, String> popularMusicMap, Language language) {
        LearningLevel[] learningLevels = LearningLevel.values();
        QuizType[] quizTypes = {QuizType.READING, QuizType.VOCABULARY, QuizType.GRAMMAR, QuizType.LISTENING};

        for (String musicId : popularMusicMap.keySet()) {
            for (LearningLevel learningLevel : learningLevels) {
                for (QuizType quizType : quizTypes) {

                    Object dto;

                    if (quizType == QuizType.LISTENING) {
                        dto = quizService.getListeningQuiz(
                                new QuizRequest(musicId, quizType, popularMusicMap.get(musicId), learningLevel, language)
                        );
                    } else {
                        dto = quizService.getQuizList(
                                new QuizRequest(musicId, quizType, popularMusicMap.get(musicId), learningLevel, language)
                        );
                    }

                    // 캐시 저장
                    String key = String.format("cache:quiz:%s:%s:%s:%s", musicId, quizType.name(), learningLevel.name(), language.name());
                    redisTemplate.opsForValue().set(key, dto, TTL_MAP.get(ChartType.daily));

                    log.info("Cached quiz list for key: {}", key);
                }
            }
        }
    }

    /**
     * 인기 차트 기반 캐시 관리 (TTL 갱신 + 신규 캐시 생성)
     */
    public void cachePopularMusicQuizLists(ChartType chartType, Language language) {
        ChartDto chart = spotifyClient.getChart(chartType.name());
        Duration ttl = TTL_MAP.getOrDefault(chartType, Duration.ofDays(30));

        List<String> musicIds = chart.getTracks().stream()
                .map(ChartDto.TrackDto::getId)
                .collect(Collectors.toList());

        // 미리 DB에서 모든 퀴즈를 조회하여 Map화 (N+1 쿼리 방지)
        Map<String, List<QuizList>> quizListMap = quizListRepository
                .findByMusicIdIn(musicIds)
                .stream()
                .collect(Collectors.groupingBy(QuizList::getMusicId));

        Map<String, List<ListeningQuiz>> listeningQuizMap = listeningQuizRepository
                .findByMusicIdIn(musicIds)
                .stream()
                .collect(Collectors.groupingBy(ListeningQuiz::getMusicId));

        for (String musicId : musicIds) {
            // Redis 캐시 존재 여부 확인
            boolean exists = hasExistingCache(musicId);

            // 존재한다면, ttl 갱신
            if (exists) {
                updateTTL(musicId, ttl);
                continue;
            }

            // 신규 캐시 생성
            createNewCachesForMusic(musicId, quizListMap, listeningQuizMap, language, ttl);
        }
    }

    /**
     * Redis에 해당 musicId 관련 캐시 존재 여부 확인 (SCAN)
     */
    private boolean hasExistingCache(String musicId) {
        String pattern = String.format("cache:quiz:%s:*", musicId);
        try (Cursor<byte[]> cursor = redisTemplate.execute(
                (RedisConnection conn) -> conn.scan(ScanOptions.scanOptions().match(pattern).count(5).build())
        )) {
            return cursor != null && cursor.hasNext();
        } catch (Exception e) {
            log.error("Redis scan failed for musicId={}", musicId, e);
            return false;
        }
    }

    /**
     * 기존 캐시 TTL 갱신
     */
    private void updateTTL(String musicId, Duration ttl) {
        String pattern = String.format("cache:quiz:%s:*", musicId);
        try (Cursor<byte[]> cursor = redisTemplate.execute(
                (RedisConnection conn) -> conn.scan(ScanOptions.scanOptions().match(pattern).count(100).build())
        )) {
            if (cursor == null) return;

            while (cursor.hasNext()) {
                String key = new String(cursor.next(), StandardCharsets.UTF_8);
                redisTemplate.expire(key, ttl);
                log.debug("Updated TTL for key={}", key);
            }
        } catch (Exception e) {
            log.error("Error updating TTL for musicId={}", musicId, e);
        }
    }

    /**
     * 신규 캐시 생성
     */
    private void createNewCachesForMusic(
            String musicId,
            Map<String, List<QuizList>> quizListMap,
            Map<String, List<ListeningQuiz>> listeningQuizMap,
            Language language,
            Duration ttl
    ) {
        for (QuizType quizType : QuizType.values()) {
            for (LearningLevel level : LearningLevel.values()) {
                Object dto = null;
                String key = null;

                if (quizType == QuizType.LISTENING) {
                    dto = listeningQuizMap.getOrDefault(musicId, Collections.emptyList())
                            .stream()
                            .filter(q -> q.getLevel() == level)
                            .findFirst()
                            .orElse(null);

                } else {
                    dto = quizListMap.getOrDefault(musicId, Collections.emptyList())
                            .stream()
                            .filter(q -> q.getQuizType() == quizType && q.getLevel() == level)
                            .findFirst()
                            .orElse(null);
                }

                if (dto != null) {
                    key = String.format("cache:quiz:%s:%s:%s:%s", musicId, quizType.name(), level.name(), language.name());
                    redisTemplate.opsForValue().set(key, dto, ttl);
                    log.debug("Cached {} for musicId={}, level={}, type={}", dto.getClass().getSimpleName(), musicId, level, quizType);
                }
            }
        }
    }
}
