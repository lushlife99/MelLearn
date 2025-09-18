package com.mellearn.be.domain.quiz.choice.submit.repository;

import com.mellearn.be.domain.quiz.choice.submit.entity.QuizSubmit;
import com.mellearn.be.domain.quiz.choice.submit.repository.jdbc.QuizSubmitJdbcRepository;
import com.mellearn.be.domain.quiz.choice.submit.repository.querydsl.QuizSubmitRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSubmitRepository extends JpaRepository<QuizSubmit, Long>, QuizSubmitRepositoryCustom, QuizSubmitJdbcRepository {

}
