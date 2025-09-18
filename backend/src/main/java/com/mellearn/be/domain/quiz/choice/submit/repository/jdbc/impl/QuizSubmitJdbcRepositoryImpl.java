package com.mellearn.be.domain.quiz.choice.submit.repository.jdbc.impl;

import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.entity.QuizSubmit;
import com.mellearn.be.domain.quiz.choice.submit.repository.jdbc.QuizSubmitJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

        String sql = "INSERT INTO quiz_submit_answer (quiz_submit_id, answer) VALUES (?, ?)";

        List<Integer> answers = submitAnswers;
        jdbcTemplate.batchUpdate(sql, answers, answers.size(),
                (ps, answer) -> {
                    ps.setLong(1, quizSubmit.getId());
                    ps.setInt(2, answer);
                });

        return new QuizSubmitDto(quizSubmit, submitAnswers);
    }
}
