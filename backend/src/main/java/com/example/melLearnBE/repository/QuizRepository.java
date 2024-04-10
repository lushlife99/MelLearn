package com.example.melLearnBE.repository;

import com.example.melLearnBE.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
