package com.example.melLearnBE.domain.quiz.choice.submit.repository;

import com.example.melLearnBE.domain.quiz.choice.submit.entity.QuizSubmit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSubmitRepository extends JpaRepository<QuizSubmit, Long> {
}
