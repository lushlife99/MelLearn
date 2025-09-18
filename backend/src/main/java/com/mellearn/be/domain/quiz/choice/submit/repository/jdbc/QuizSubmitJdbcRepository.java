package com.mellearn.be.domain.quiz.choice.submit.repository.jdbc;

import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.entity.QuizSubmit;

import java.util.List;

public interface QuizSubmitJdbcRepository {
    QuizSubmitDto bulkSaveQuizSubmitAnswer(QuizSubmit quizSubmit, List<Integer> submitAnswers);
}
