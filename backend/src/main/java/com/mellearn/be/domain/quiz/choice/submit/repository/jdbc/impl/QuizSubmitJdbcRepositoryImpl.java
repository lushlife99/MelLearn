package com.mellearn.be.domain.quiz.choice.submit.repository.jdbc.impl;

import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.entity.QuizSubmit;
import com.mellearn.be.domain.quiz.choice.submit.repository.jdbc.QuizSubmitJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class QuizSubmitJdbcRepositoryImpl implements QuizSubmitJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public QuizSubmitDto bulkSaveQuizSubmitAnswer(QuizSubmit quizSubmit, List<Integer> submitAnswers) {
        if (quizSubmit.getId() == null) {
            throw new IllegalArgumentException("QuizSubmit ID must not be null. Save QuizSubmit entity first.");
        }

        StringBuilder sb = new StringBuilder("INSERT INTO quiz_submit_answer (quiz_submit_id, answer, answer_order) VALUES ");
        List<Object> params = new ArrayList<>();
        for (int i = 0; i < submitAnswers.size(); i++) {
            sb.append("(?, ?, ?)");
            if (i < submitAnswers.size() - 1) sb.append(", ");
            params.add(quizSubmit.getId());
            params.add(submitAnswers.get(i));
            params.add(i);
        }
        jdbcTemplate.update(sb.toString(), params.toArray());
        return new QuizSubmitDto(quizSubmit, submitAnswers);
    }
}
