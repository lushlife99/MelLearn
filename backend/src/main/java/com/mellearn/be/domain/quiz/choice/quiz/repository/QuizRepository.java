package com.mellearn.be.domain.quiz.choice.quiz.repository;

import com.mellearn.be.domain.quiz.choice.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

}
