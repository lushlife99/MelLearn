package com.mellearn.be.domain.quiz.choice.quiz.repository.querydsl;

import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface QuizListRepositoryCustom {

    Optional<QuizList> findByMusicIdAndLevelAndQuizType(String musicId, QuizType quizType, LearningLevel level);
}
