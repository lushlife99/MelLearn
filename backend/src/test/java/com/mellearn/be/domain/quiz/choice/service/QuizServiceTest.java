package com.mellearn.be.domain.quiz.choice.service;

import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.mellearn.be.domain.quiz.choice.quiz.repository.QuizListRepository;
import com.mellearn.be.domain.quiz.choice.quiz.service.QuizCreateService;
import com.mellearn.be.domain.quiz.choice.quiz.service.QuizService;
import com.mellearn.be.domain.quiz.listening.quiz.dto.ListeningQuizDto;
import com.mellearn.be.domain.quiz.listening.quiz.entity.ListeningQuiz;
import com.mellearn.be.domain.quiz.listening.quiz.repository.ListeningQuizRepository;
import com.mellearn.be.global.error.CustomException;
import com.mellearn.be.global.error.enums.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizListRepository quizListRepository;

    @Mock
    private ListeningQuizRepository listeningQuizRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private QuizCreateService quizCreateService;

    @InjectMocks
    private QuizService quizService;

    private QuizRequest quizRequest;
    private String memberId;
    private Member member;
    private QuizList quizList;
    private ListeningQuiz listeningQuiz;

    @BeforeEach
    void setUp() {
        quizRequest = new QuizRequest(
                "test-music",
                QuizType.READING,
                "test-lyric",
                LearningLevel.Advanced,
                Language.ENGLISH
        );

        memberId = "test-member";
        member = Member.builder()
                .id(1L)
                .memberId(memberId)
                .level(LearningLevel.Advanced)
                .build();

        quizList = QuizList.builder()
                .id(1L)
                .musicId("test-music")
                .quizType(QuizType.READING)
                .level(LearningLevel.Advanced)
                .quizzes(new ArrayList<>())
                .build();

        listeningQuiz = ListeningQuiz.builder()
                .id(1L)
                .musicId("test-music")
                .level(LearningLevel.Advanced)
                .blankedText("테스트 텍스트")
                .answerList(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("일반 퀴즈 목록 조회 테스트 - 기존 퀴즈")
    void getQuizList_ExistingQuiz() throws Exception {
        // 캐시 mock
        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache("quizListCache")).thenReturn(mockCache);
        when(mockCache.get(anyString(), eq(QuizListDto.class))).thenReturn(null); // 캐시 미스

        when(quizListRepository.findByMusicIdAndLevelAndQuizType(
                eq("test-music"),
                eq(QuizType.READING),
                eq(LearningLevel.Advanced)
        )).thenReturn(Optional.of(quizList));

        CompletableFuture<QuizListDto> result =
                quizService.getQuizList(quizRequest);

        assertNotNull(result);
        assertEquals(quizList.getId(), result.get().getId());
    }



    @Test
    @DisplayName("듣기 퀴즈 조회 테스트 - 기존 퀴즈")
    void getListeningQuiz_ExistingQuiz() throws Exception {
        // given
        quizRequest.setQuizType(QuizType.LISTENING);
        when(listeningQuizRepository.findByMusicIdAndLevel(
                eq("test-music"),
                eq(LearningLevel.Advanced)
        )).thenReturn(Optional.of(listeningQuiz));

        // when
        CompletableFuture<ListeningQuizDto> result = quizService.getListeningQuiz(quizRequest);

        // then
        assertNotNull(result);
        ListeningQuizDto listeningQuizDto = result.get();
        assertNotNull(listeningQuizDto);
        assertEquals(listeningQuiz.getId(), listeningQuizDto.getId());
        verify(listeningQuizRepository).findByMusicIdAndLevel(
                eq("test-music"),
                eq(LearningLevel.Advanced)
        );
    }

    @Test
    @DisplayName("일반 퀴즈 목록 조회 테스트 - 퀴즈가 존재하지 않으면 Redis에 추가 후 Not Found Exception 발생")
    void getQuizList_CreateNewQuiz() {
        // 캐시 mock
        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache("quizListCache")).thenReturn(mockCache);
        when(mockCache.get(anyString(), eq(QuizListDto.class))).thenReturn(null); // 캐시 미스

        when(quizListRepository.findByMusicIdAndLevelAndQuizType(
                eq("test-music"),
                eq(QuizType.READING),
                eq(LearningLevel.Advanced)
        )).thenReturn(Optional.empty());

        // Redis mock
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        CompletableFuture<QuizListDto> future = quizService.getQuizList(quizRequest);

        // then
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(CustomException.class, exception.getCause());
        assertEquals(ErrorCode.QUIZ_NOT_FOUND, ((CustomException) exception.getCause()).getErrorCode());

        // Redis에 저장됐는지 검증
        verify(valueOperations).set(anyString(), eq(quizRequest), anyLong(), any());
    }

    @Test
    @DisplayName("듣기 퀴즈 조회 테스트 - 퀴즈가 존재하지 않으면 Redis에 추가 후 Not Found Exception 발생")
    void getListeningQuiz_CreateNewQuiz() {
        quizRequest.setQuizType(QuizType.LISTENING);

        when(listeningQuizRepository.findByMusicIdAndLevel(
                eq("test-music"),
                eq(LearningLevel.Advanced)
        )).thenReturn(Optional.empty());

        // 캐시 mock
        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache("quizListCache")).thenReturn(mockCache);
        when(mockCache.get(anyString(), eq(ListeningQuizDto.class))).thenReturn(null);

        // Redis mock
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        CompletableFuture<ListeningQuizDto> future = quizService.getListeningQuiz(quizRequest);

        // then
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(CustomException.class, exception.getCause());
        assertEquals(ErrorCode.QUIZ_NOT_FOUND, ((CustomException) exception.getCause()).getErrorCode());

        // Redis에 저장됐는지 검증
        verify(valueOperations).set(anyString(), eq(quizRequest), anyLong(), any());
    }

}
