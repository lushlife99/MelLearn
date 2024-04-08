package com.example.melLearnBE.repository;

import com.example.melLearnBE.enums.QuizType;
import com.example.melLearnBE.model.QuizList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizListRepository extends JpaRepository<QuizList, Long> {

    Optional<QuizList> findByMusicIdAndQuizTypeAndLevel(String musicId, QuizType quizType, int level);
}
