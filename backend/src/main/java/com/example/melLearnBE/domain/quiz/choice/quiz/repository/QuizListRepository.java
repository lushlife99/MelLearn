package com.example.melLearnBE.domain.quiz.choice.quiz.repository;

import com.example.melLearnBE.domain.member.enums.LearningLevel;
import com.example.melLearnBE.domain.quiz.choice.quiz.entity.QuizList;
import com.example.melLearnBE.enums.QuizType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizListRepository extends JpaRepository<QuizList, Long> {

    Optional<QuizList> findByMusicIdAndQuizTypeAndLevel(String musicId, QuizType quizType, LearningLevel level);
}
