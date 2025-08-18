package com.mellearn.be.domain.quiz.choice.quiz.repository;


import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.mellearn.be.domain.quiz.choice.quiz.repository.querydsl.QuizListRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizListRepository extends JpaRepository<QuizList, Long>, QuizListRepositoryCustom {


}
