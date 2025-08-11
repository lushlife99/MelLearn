package com.mellearn.be.domain.quiz.choice.service;

import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.quiz.listening.quiz.dto.ListeningQuizDto;
import com.mellearn.be.domain.quiz.listening.quiz.entity.ListeningQuiz;
import com.mellearn.be.domain.quiz.listening.quiz.repository.ListeningQuizRepository;
import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.member.repository.MemberRepository;
import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.repository.QuizListRepository;
import com.mellearn.be.domain.quiz.choice.quiz.service.QuizCreateService;
import com.mellearn.be.domain.quiz.choice.quiz.service.QuizService;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
        quizRequest = new QuizRequest();
        quizRequest.setMusicId("test-music");
        quizRequest.setQuizType(QuizType.READING);

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
        // given
        when(quizListRepository.findByMusicIdAndQuizTypeAndLevel(
                eq("test-music"),
                eq(QuizType.READING),
                eq(LearningLevel.Advanced)
        )).thenReturn(Optional.of(quizList));

        // when
        CompletableFuture<QuizListDto> result = quizService.getQuizList(quizRequest, LearningLevel.Advanced, Language.ENGLISH);

        // then
        assertNotNull(result);
        QuizListDto quizListDto = result.get();
        assertNotNull(quizListDto);
        assertEquals(quizList.getId(), quizListDto.getId());
        verify(quizListRepository).findByMusicIdAndQuizTypeAndLevel(
                eq("test-music"),
                eq(QuizType.READING),
                eq(LearningLevel.Advanced)
        );
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
        CompletableFuture<ListeningQuizDto> result = quizService.getListeningQuiz(quizRequest, LearningLevel.Advanced, Language.ENGLISH);

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
    @DisplayName("일반 퀴즈 목록 조회 테스트 - 새로운 퀴즈 생성")
    void getQuizList_CreateNewQuiz() throws Exception {
        // given
        QuizListDto expectedDto = new QuizListDto(quizList);
        when(quizListRepository.findByMusicIdAndQuizTypeAndLevel(
                eq("test-music"),
                eq(QuizType.READING),
                eq(LearningLevel.Advanced)
        )).thenReturn(Optional.empty());
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(quizCreateService.createChoiceQuiz(eq(quizRequest), eq(member.getLevel()), eq(Language.ENGLISH)))
                .thenReturn(expectedDto);

        // when
        CompletableFuture<QuizListDto> result = quizService.getQuizList(quizRequest, LearningLevel.Advanced, Language.ENGLISH);

        // then
        assertNotNull(result);
        QuizListDto quizListDto = result.get();
        assertNotNull(quizListDto);
        assertEquals(expectedDto.getId(), quizListDto.getId());
        verify(valueOperations).set(anyString(), eq("true"), eq(1L), eq(TimeUnit.MINUTES));
        verify(quizCreateService).createChoiceQuiz(eq(quizRequest), eq(member.getLevel()), eq(Language.ENGLISH));
    }

    @Test
    @DisplayName("듣기 퀴즈 조회 테스트 - 새로운 퀴즈 생성")
    void getListeningQuiz_CreateNewQuiz() throws Exception {
        // given
        quizRequest.setQuizType(QuizType.LISTENING);
        ListeningQuizDto expectedDto = new ListeningQuizDto(listeningQuiz);
        when(listeningQuizRepository.findByMusicIdAndLevel(
                eq("test-music"),
                eq(LearningLevel.Advanced)
        )).thenReturn(Optional.empty());
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(quizCreateService.createListeningQuiz(eq(quizRequest), eq(member.getLevel()), eq(Language.ENGLISH)))
                .thenReturn(expectedDto);

        // when
        CompletableFuture<ListeningQuizDto> result = quizService.getListeningQuiz(quizRequest, LearningLevel.Advanced, Language.ENGLISH);

        // then
        assertNotNull(result);
        ListeningQuizDto listeningQuizDto = result.get();
        assertNotNull(listeningQuizDto);
        assertEquals(expectedDto.getId(), listeningQuizDto.getId());
        verify(valueOperations).set(anyString(), eq("true"), eq(1L), eq(TimeUnit.MINUTES));
        verify(quizCreateService).createListeningQuiz(eq(quizRequest), eq(member.getLevel()), eq(Language.ENGLISH));
    }
}
