package com.mellearn.be.domain.quiz.choice.submit.service;

import com.mellearn.be.domain.quiz.listening.quiz.entity.ListeningQuiz;
import com.mellearn.be.domain.quiz.listening.quiz.repository.ListeningQuizRepository;
import com.mellearn.be.domain.quiz.listening.submit.dto.ListeningSubmitDto;
import com.mellearn.be.domain.quiz.listening.submit.dto.request.ListeningSubmitRequest;
import com.mellearn.be.domain.quiz.listening.submit.entity.ListeningSubmit;
import com.mellearn.be.domain.quiz.listening.submit.repository.ListeningSubmitRepository;
import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.entity.Quiz;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.repository.QuizListRepository;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitRequest;
import com.mellearn.be.domain.quiz.choice.submit.entity.QuizSubmit;
import com.mellearn.be.domain.quiz.choice.submit.repository.QuizSubmitRepository;
import com.mellearn.be.global.error.CustomException;
import com.mellearn.be.global.error.enums.ErrorCode;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizSubmitService {

    private static final String QUIZ_TOTAL_SUBMIT_KEY = "quiz:submitCount:";
    private static final String QUIZ_CORRECT_SUBMIT_KEY = "quiz:correctCount:";

    private final ListeningQuizRepository listeningQuizRepository;
    private final ListeningSubmitRepository listeningSubmitRepository;
    private final QuizListRepository quizListRepository;
    private final QuizSubmitRepository quizSubmitRepository;
    private final StringRedisTemplate redisTemplate;


    @Transactional
    public ListeningSubmitDto submitListeningQuiz(ListeningSubmitRequest submitRequest, Member member) {
        ListeningQuiz listeningQuiz = findListeningQuiz(submitRequest.getMusicId(), member.getLevel());
        validateAnswerListSize(listeningQuiz.getAnswerList(), submitRequest.getSubmitWordList());
        
        List<String> submitWordList = submitRequest.getSubmitWordList();
        int correctCount = calculateListeningCorrectCount(listeningQuiz.getAnswerList(), submitWordList);
        
        ListeningSubmit listeningSubmit = createListeningSubmit(listeningQuiz, submitWordList, member, correctCount);
        return new ListeningSubmitDto(listeningSubmitRepository.save(listeningSubmit));
    }

    @Transactional
    public QuizSubmitDto submitQuiz(QuizSubmitRequest submitRequest, Member member) {
        QuizList quizList = findQuizList(submitRequest.getMusicId(), submitRequest.getQuizType(), member.getLevel());
        if (submitRequest.getAnswers().size() != quizList.getQuizzes().size()) {
            throw new CustomException(ErrorCode.REQUEST_ARRAY_SIZE_NOT_MATCHED);
        }
        double score = calculateQuizScore(submitRequest, quizList);
        
        QuizSubmit quizSubmit = QuizSubmit.builder()
                .quizList(quizList)
                .member(member)
                .score((int) score)
                .build();


        QuizSubmit savedQuizSubmit = quizSubmitRepository.save(quizSubmit);
        return quizSubmitRepository.bulkSaveQuizSubmitAnswer(savedQuizSubmit, submitRequest.getAnswers());
    }

    private ListeningQuiz findListeningQuiz(String musicId, LearningLevel level) {
        return listeningQuizRepository.findByMusicIdAndLevel(musicId, level)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
    }

    private QuizList findQuizList(String musicId, QuizType quizType, LearningLevel level) {
        return quizListRepository.findByMusicIdAndLevelAndQuizType(musicId, quizType, level)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
    }

    private void validateAnswerListSize(List<String> answerList, List<String> submitWordList) {
        if (answerList.size() != submitWordList.size()) {
            log.error("Answer list size mismatch: expected {}, got {}", answerList.size(), submitWordList.size());
            throw new CustomException(ErrorCode.REQUEST_ARRAY_SIZE_NOT_MATCHED);
        }
    }

    private int calculateListeningCorrectCount(List<String> answerList, List<String> submitWordList) {
        int correctCount = 0;
        for (int i = 0; i < answerList.size(); i++) {
            String answerWord = answerList.get(i);
            String submitWord = submitWordList.get(i);

            if (answerWord.equals(submitWord.trim())) {
                submitWordList.set(i, answerWord);
                correctCount++;
            }
        }
        return correctCount;
    }

    private ListeningSubmit createListeningSubmit(ListeningQuiz listeningQuiz, List<String> submitWordList, 
            Member member, int correctCount) {
        return ListeningSubmit.builder()
                .listeningQuiz(listeningQuiz)
                .submitAnswerList(submitWordList)
                .level(member.getLevel())
                .createdTime(LocalDateTime.now())
                .member(member)
                .score(calculateScore(correctCount, submitWordList.size()))
                .build();
    }

    private double calculateQuizScore(QuizSubmitRequest submitRequest, QuizList quizList) {
        List<Integer> submitAnswers = submitRequest.getAnswers();
        List<Quiz> quizzes = quizList.getQuizzes();
        final int[] totalCorrectCount = new int[1];

        final var stringSerializer = redisTemplate.getStringSerializer();
        final byte[] totalSubmitField = stringSerializer.serialize("totalSubmit");
        final byte[] correctSubmitField = stringSerializer.serialize("correctSubmit");

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (int i = 0; i < quizzes.size(); i++) {
                Quiz quiz = quizzes.get(i);
                byte[] key = stringSerializer.serialize("quiz:" + quiz.getId());

                connection.hIncrBy(key, totalSubmitField, 1);

                if (quiz.getAnswer() == submitAnswers.get(i)) {
                    connection.hIncrBy(key, correctSubmitField, 1);
                    totalCorrectCount[0]++;
                }
            }
            return null;
        });

        return calculateScore(totalCorrectCount[0], quizzes.size());
    }

    private double calculateScore(int correctCount, int totalCount) {
        return (double) correctCount * 100 / totalCount;
    }
}
