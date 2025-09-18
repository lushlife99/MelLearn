package com.mellearn.be.domain.quiz.choice.quiz.repository.querydsl.impl;

import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.quiz.entity.Quiz;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.mellearn.be.domain.quiz.choice.quiz.repository.querydsl.QuizListRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.mellearn.be.domain.quiz.choice.quiz.entity.QQuiz.quiz;
import static com.mellearn.be.domain.quiz.choice.quiz.entity.QQuizList.quizList;

@Repository
public class QuizListRepositoryImpl implements QuizListRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public QuizListRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuizList> findByMusicIdAndLevelAndQuizType(String musicId, QuizType quizType, LearningLevel level) {
        QuizList ql = queryFactory.selectFrom(quizList)
                .leftJoin(quizList.quizzes, quiz).fetchJoin()
                .where(
                        quizList.musicId.eq(musicId),
                        quizList.level.eq(level),
                        quizList.quizType.eq(quizType)
                )
                .fetchOne();

        ql.getQuizzes().forEach(q -> Hibernate.initialize(q.getOptionList()));

        return Optional.ofNullable(ql);
    }
}
