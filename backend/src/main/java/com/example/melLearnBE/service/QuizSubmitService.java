package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.model.ListeningSubmitDto;
import com.example.melLearnBE.dto.model.QuizSubmitDto;
import com.example.melLearnBE.dto.request.ListeningSubmitRequest;
import com.example.melLearnBE.dto.request.QuizSubmitRequest;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.enums.LearningLevel;
import com.example.melLearnBE.enums.QuizType;
import com.example.melLearnBE.model.*;
import com.example.melLearnBE.repository.ListeningQuizRepository;
import com.example.melLearnBE.repository.ListeningSubmitRepository;
import com.example.melLearnBE.repository.QuizListRepository;
import com.example.melLearnBE.repository.QuizSubmitRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizSubmitService {

    private final ListeningQuizRepository listeningQuizRepository;
    private final ListeningSubmitRepository listeningSubmitRepository;
    private final QuizListRepository quizListRepository;
    private final QuizSubmitRepository quizSubmitRepository;

    private static final int QUIZ_COUNT = 4;

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
        double score = calculateQuizScore(submitRequest, quizList);
        
        QuizSubmit quizSubmit = createQuizSubmit(quizList, submitRequest.getAnswers(), member, score);
        return new QuizSubmitDto(quizSubmitRepository.save(quizSubmit));
    }

    private ListeningQuiz findListeningQuiz(String musicId, LearningLevel level) {
        return listeningQuizRepository.findByMusicIdAndLevel(musicId, level)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
    }

    private QuizList findQuizList(String musicId, QuizType quizType, LearningLevel level) {
        return quizListRepository.findByMusicIdAndQuizTypeAndLevel(musicId, quizType, level)
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
        int totalCorrectCount = 0;

        for (int i = 0; i < quizzes.size(); i++) {
            Quiz quiz = quizzes.get(i);
            updateQuizStats(quiz, submitAnswers.get(i));
            if (quiz.getAnswer() == submitAnswers.get(i)) {
                totalCorrectCount++;
            }
        }

        return calculateScore(totalCorrectCount, quizzes.size());
    }

    private void updateQuizStats(Quiz quiz, int submittedAnswer) {
        quiz.setSubmitCount(quiz.getSubmitCount() + 1);
        if (quiz.getAnswer() == submittedAnswer) {
            quiz.setCorrectCount(quiz.getCorrectCount() + 1);
        }
    }

    private QuizSubmit createQuizSubmit(QuizList quizList, List<Integer> submitAnswers, Member member, double score) {
        return QuizSubmit.builder()
                .quizList(quizList)
                .submitAnswerList(submitAnswers)
                .member(member)
                .score(score)
                .build();
    }

    private double calculateScore(int correctCount, int totalCount) {
        return (double) correctCount * 100 / totalCount;
    }
}
