package com.mellearn.be.domain.quiz.choice.quiz.batch.service;

import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.mellearn.be.domain.quiz.choice.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizListCacheBatchService {

    private final QuizService quizService;
    private final CacheManager cacheManager;

    /**
     * 인기 음악 ID 리스트를 받아서 각각 캐시에 저장한다.
     */
    public void cachePopularMusicQuizLists(Map<String, String> popularMusicMap, Language language) {
        Cache cache = cacheManager.getCache("quizListCache");
        if (cache == null) {
            throw new IllegalStateException("Cache 'quizListCache' not configured");
        }

        LearningLevel[] learningLevels = LearningLevel.values(); // 3가지 레벨 모두
        QuizType[] quizTypes = {QuizType.READING, QuizType.VOCABULARY, QuizType.GRAMMAR, QuizType.LISTENING};

        for (String musicId : popularMusicMap.keySet()) {
            for (LearningLevel learningLevel : learningLevels) {
                for (QuizType quizType : quizTypes) {
                    try {
                        CompletableFuture<?> future;

                        if (quizType == QuizType.LISTENING) {
                            future = quizService.getListeningQuiz(
                                    new QuizRequest(musicId, quizType, popularMusicMap.get(musicId)),
                                    learningLevel,
                                    language
                            );
                        } else {
                            future = quizService.getQuizList(
                                    new QuizRequest(musicId, quizType, popularMusicMap.get(musicId)),
                                    learningLevel,
                                    language
                            );
                        }

                        // 비동기 결과 기다림
                        Object dto = future.get();

                        // 캐시 저장
                        String key = musicId + "_" + quizType.name() + "_" + learningLevel.name() + "_" + language.name();
                        cache.put(key, dto);

                        log.info("Cached quiz list for key: {}", key);

                    } catch (Exception e) {
                        log.error("Failed to cache quiz list for musicId: {}, learningLevel: {}, quizType: {}", musicId, learningLevel, quizType);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
