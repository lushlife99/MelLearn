package com.mellearn.be.domain.quiz.comprehensive.service;

import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.mellearn.be.domain.quiz.choice.quiz.repository.QuizListRepository;
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

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComprehensiveQuizServiceTest {

        @Mock
        private QuizListRepository quizListRepository;

        @Mock
        private ListeningQuizRepository listeningQuizRepository;

        @Mock
        private RedisTemplate<String, Object> redisTemplate;

        @Mock
        private ValueOperations<String, Object> valueOperations;

        @InjectMocks
        private QuizService quizService;

        private QuizRequest quizRequest;
        private QuizList quizList;
        private ListeningQuiz listeningQuiz;

        @BeforeEach
        void setUp() {
                quizRequest = new QuizRequest(
                                "test-music",
                                QuizType.READING,
                                "test-lyric",
                                LearningLevel.Advanced,
                                Language.ENGLISH);

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
        @DisplayName("동기 퀴즈 목록 조회 - 기존 퀴즈")
        void getQuizList_ExistingQuiz() {
                when(redisTemplate.opsForValue()).thenReturn(valueOperations);
                when(valueOperations.get(anyString())).thenReturn(null);

                when(quizListRepository.findByMusicIdAndLevelAndQuizType(
                                eq("test-music"),
                                eq(QuizType.READING),
                                eq(LearningLevel.Advanced))).thenReturn(Optional.of(quizList));

                QuizListDto result = quizService.getQuizList(quizRequest);

                assertNotNull(result);
                assertEquals(quizList.getId(), result.getId());
        }

        @Test
        @DisplayName("동기 듣기 퀴즈 조회 - 기존 퀴즈")
        void getListeningQuiz_ExistingQuiz() {
                quizRequest.setQuizType(QuizType.LISTENING);

                when(redisTemplate.opsForValue()).thenReturn(valueOperations);
                when(valueOperations.get(anyString())).thenReturn(null);

                when(listeningQuizRepository.findByMusicIdAndLevel(
                                eq("test-music"),
                                eq(LearningLevel.Advanced))).thenReturn(Optional.of(listeningQuiz));

                ListeningQuizDto result = quizService.getListeningQuiz(quizRequest);

                assertNotNull(result);
                assertEquals(listeningQuiz.getId(), result.getId());
                verify(listeningQuizRepository).findByMusicIdAndLevel(
                                eq("test-music"),
                                eq(LearningLevel.Advanced));
        }

        @Test
        @DisplayName("동기 퀴즈 목록 조회 - 퀴즈가 없으면 Redis에 저장 후 예외")
        void getQuizList_QuizNotFound() {

                when(quizListRepository.findByMusicIdAndLevelAndQuizType(
                                eq("test-music"),
                                eq(QuizType.READING),
                                eq(LearningLevel.Advanced))).thenReturn(Optional.empty());

                when(redisTemplate.opsForValue()).thenReturn(valueOperations);
                when(valueOperations.get(anyString())).thenReturn(null);

                CustomException exception = assertThrows(CustomException.class,
                                () -> quizService.getQuizList(quizRequest));

                assertEquals(ErrorCode.QUIZ_NOT_FOUND, exception.getErrorCode());

                verify(valueOperations).set(anyString(), eq(quizRequest), anyLong(), any());
        }

        @Test
        @DisplayName("동기 듣기 퀴즈 조회 - 퀴즈가 없으면 Redis에 저장 후 예외")
        void getListeningQuiz_QuizNotFound() {
                quizRequest.setQuizType(QuizType.LISTENING);

                when(listeningQuizRepository.findByMusicIdAndLevel(
                                eq("test-music"),
                                eq(LearningLevel.Advanced))).thenReturn(Optional.empty());

                when(redisTemplate.opsForValue()).thenReturn(valueOperations);
                when(valueOperations.get(anyString())).thenReturn(null);

                CustomException exception = assertThrows(CustomException.class,
                                () -> quizService.getListeningQuiz(quizRequest));

                assertEquals(ErrorCode.QUIZ_NOT_FOUND, exception.getErrorCode());

                verify(valueOperations).set(anyString(), eq(quizRequest), anyLong(), any());
        }
}
