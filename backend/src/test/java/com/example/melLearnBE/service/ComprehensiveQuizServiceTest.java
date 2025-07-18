package com.example.melLearnBE.service;

import com.example.melLearnBE.domain.quiz.comprehensive.dto.ComprehensiveQuizSubmitRequest;
import com.example.melLearnBE.domain.quiz.comprehensive.service.ComprehensiveQuizService;
import com.example.melLearnBE.domain.listening.quiz.dto.ListeningQuizDto;
import com.example.melLearnBE.domain.listening.submit.dto.ListeningSubmitDto;
import com.example.melLearnBE.domain.listening.quiz.entity.ListeningQuiz;
import com.example.melLearnBE.domain.member.entity.Member;
import com.example.melLearnBE.domain.music.dto.LrcLyric;
import com.example.melLearnBE.domain.quiz.choice.quiz.entity.Quiz;
import com.example.melLearnBE.domain.quiz.choice.quiz.entity.QuizList;
import com.example.melLearnBE.domain.quiz.choice.quiz.service.QuizService;
import com.example.melLearnBE.domain.quiz.speaking.entity.SpeakingSubmit;
import com.example.melLearnBE.domain.quiz.speaking.service.SpeakingService;
import com.example.melLearnBE.dto.model.*;
import com.example.melLearnBE.dto.request.*;
import com.example.melLearnBE.domain.quiz.comprehensive.dto.ComprehensiveQuizDto;
import com.example.melLearnBE.domain.quiz.comprehensive.dto.ComprehensiveQuizSubmitDto;
import com.example.melLearnBE.domain.member.enums.LearningLevel;
import com.example.melLearnBE.enums.QuizType;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.domain.listening.quiz.repository.ListeningQuizRepository;
import com.example.melLearnBE.domain.listening.submit.repository.ListeningSubmitRepository;
import com.example.melLearnBE.domain.member.repository.MemberRepository;
import com.example.melLearnBE.domain.quiz.choice.quiz.repository.QuizListRepository;
import com.example.melLearnBE.domain.quiz.choice.submit.repository.QuizSubmitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ComprehensiveQuizServiceTest {

    @Mock
    private QuizService quizService;

    @Mock
    private SpeakingService speakingService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private QuizListRepository quizListRepository;

    @Mock
    private ListeningQuizRepository listeningQuizRepository;

    @Mock
    private QuizSubmitRepository quizSubmitRepository;

    @Mock
    private ListeningSubmitRepository listeningSubmitRepository;

    @InjectMocks
    private ComprehensiveQuizService comprehensiveQuizService;

    private Member testMember;
    private QuizRequest testQuizRequest;
    private ComprehensiveQuizSubmitRequest testSubmitRequest;
    private MultipartFile testSpeakingFile;
    private List<LrcLyric> testLrcLyrics;
    private List<Integer> testAnswers;
    private List<String> testListeningAnswers;
    private QuizList testQuizList;
    private ListeningQuiz testListeningQuiz;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .memberId("testMemberId")
                .level(LearningLevel.Beginner)
                .build();

        testLrcLyrics = Arrays.asList(
                new LrcLyric(0L, 1000L, "Hello"),
                new LrcLyric(1000L, 2000L, "World")
        );

        testAnswers = Arrays.asList(1, 2);
        testListeningAnswers = Arrays.asList("answer1", "answer2");

        testQuizRequest = QuizRequest.builder()
                .musicId("testMusicId")
                .lyric("Test lyric")
                .build();

        testSubmitRequest = new ComprehensiveQuizSubmitRequest();
        testSubmitRequest.setMusicId("testMusicId");
        testSubmitRequest.setLrcLyricList(testLrcLyrics);
        testSubmitRequest.setGrammarSubmit(testAnswers);
        testSubmitRequest.setVocabularySubmit(testAnswers);
        testSubmitRequest.setReadingSubmit(testAnswers);
        testSubmitRequest.setListeningSubmit(testListeningAnswers);

        testSpeakingFile = new MockMultipartFile(
                "audio",
                "test.mp3",
                "audio/mpeg",
                "test audio content".getBytes()
        );

        testQuizList = QuizList.builder()
                .id(1L)
                .musicId("testMusicId")
                .level(LearningLevel.Beginner)
                .quizType(QuizType.GRAMMAR)
                .quizzes(new ArrayList<>())
                .build();
        
        testListeningQuiz = ListeningQuiz.builder()
                .id(1L)
                .musicId("testMusicId")
                .level(LearningLevel.Beginner)
                .blankedText("Test blanked text")
                .answerList(testListeningAnswers)
                .build();
    }

    @Test
    @DisplayName("종합 퀴즈 제출 테스트 - 성공")
    void submit_Success() throws Exception {
        // given
        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.of(testMember));

        SpeakingSubmit speakingSubmit = SpeakingSubmit.builder()
                .id(1L)
                .musicId("testMusicId")
                .submit("test submit")
                .markedText("test marked text")
                .score(85.5)
                .createdTime(LocalDateTime.now())
                .build();
        SpeakingSubmitDto speakingSubmitDto = new SpeakingSubmitDto(speakingSubmit);

        QuizSubmitDto grammarSubmitDto = QuizSubmitDto.builder()
                .id(2L)
                .score(90.0)
                .createdTime(LocalDateTime.now())
                .build();

        QuizSubmitDto vocaSubmitDto = QuizSubmitDto.builder()
                .id(3L)
                .score(85.0)
                .createdTime(LocalDateTime.now())
                .build();

        QuizSubmitDto readingSubmitDto = QuizSubmitDto.builder()
                .id(4L)
                .score(95.0)
                .createdTime(LocalDateTime.now())
                .build();

        ListeningSubmitDto listeningSubmitDto = new ListeningSubmitDto(
                5L, 
                new ListeningQuizDto(testListeningQuiz),
                LearningLevel.Beginner,
                testListeningAnswers, 80.0, LocalDateTime.now()
        );

        when(speakingService.submit(any(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(speakingSubmitDto));
        when(quizService.submit(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(grammarSubmitDto))
                .thenReturn(CompletableFuture.completedFuture(vocaSubmitDto))
                .thenReturn(CompletableFuture.completedFuture(readingSubmitDto));
        when(quizService.listeningSubmit(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(listeningSubmitDto));

        // when
        ComprehensiveQuizSubmitDto result = comprehensiveQuizService.submit(testSubmitRequest, testSpeakingFile, "testMemberId");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMusicId()).isEqualTo("testMusicId");
        assertThat(result.getLevel()).isEqualTo(LearningLevel.Beginner);
        assertThat(result.getComprehensiveQuizAnswer().getSpeakingSubmit().getScore()).isEqualTo(85.5);
        assertThat(result.getComprehensiveQuizAnswer().getGrammarSubmit().getScore()).isEqualTo(90.0);
        assertThat(result.getComprehensiveQuizAnswer().getVocabularySubmit().getScore()).isEqualTo(85.0);
        assertThat(result.getComprehensiveQuizAnswer().getReadingSubmit().getScore()).isEqualTo(95.0);
        assertThat(result.getComprehensiveQuizAnswer().getListeningSubmit().getScore()).isEqualTo(80.0);
    }

    @Test
    @DisplayName("종합 퀴즈 조회 테스트 - 성공")
    void get_Success() throws Exception {
        // given
        ListeningQuizDto listeningQuizDto = new ListeningQuizDto(testListeningQuiz);

        List<Quiz> quizzes = new ArrayList<>();
        Quiz quiz = Quiz.builder()
                .id(1L)
                .question("Test question")
                .optionList(Arrays.asList("option1", "option2", "option3", "option4"))
                .answer(1)
                .comment("Test comment")
                .build();
        quizzes.add(quiz);
        testQuizList.setQuizzes(quizzes);

        QuizListDto grammarQuizListDto = new QuizListDto(testQuizList);
        QuizListDto vocaQuizListDto = new QuizListDto(testQuizList);
        QuizListDto readingQuizListDto = new QuizListDto(testQuizList);

        when(quizService.getQuizList(any(), anyString())).thenReturn(CompletableFuture.completedFuture(grammarQuizListDto))
                .thenReturn(CompletableFuture.completedFuture(vocaQuizListDto))
                .thenReturn(CompletableFuture.completedFuture(readingQuizListDto));
        when(quizService.getListeningQuiz(any(), anyString())).thenReturn(CompletableFuture.completedFuture(listeningQuizDto));

        // when
        ComprehensiveQuizDto result = comprehensiveQuizService.get(testQuizRequest, "testMemberId");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGrammarQuiz()).isNotNull();
        assertThat(result.getVocaQuiz()).isNotNull();
        assertThat(result.getReadingQuiz()).isNotNull();
        assertThat(result.getListeningQuizDto()).isNotNull();
    }

    @Test
    @DisplayName("종합 퀴즈 제출 테스트 - 존재하지 않는 회원")
    void submit_NonExistingMember_ShouldThrowException() {
        // given
        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () ->
                comprehensiveQuizService.submit(testSubmitRequest, testSpeakingFile, "invalidMemberId")
        );
    }
} 