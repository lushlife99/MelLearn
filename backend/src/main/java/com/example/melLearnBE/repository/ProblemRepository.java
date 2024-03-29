package com.example.melLearnBE.repository;

import com.example.melLearnBE.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
}
