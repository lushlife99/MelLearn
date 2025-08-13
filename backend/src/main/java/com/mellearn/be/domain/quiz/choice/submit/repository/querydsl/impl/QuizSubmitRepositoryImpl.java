package com.mellearn.be.domain.quiz.choice.submit.repository.querydsl.impl;

import com.mellearn.be.domain.quiz.choice.quiz.entity.QQuizList;
import com.mellearn.be.domain.quiz.choice.submit.entity.QuizSubmit;
import com.mellearn.be.domain.quiz.choice.submit.repository.querydsl.QuizSubmitRepositoryCustom;
import com.mellearn.be.domain.quiz.listening.submit.dto.ListeningSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.listening.submit.entity.ListeningSubmit;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitDto;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.mellearn.be.domain.quiz.choice.quiz.entity.QQuizList.quizList;
import static com.mellearn.be.domain.quiz.choice.submit.entity.QQuizSubmit.quizSubmit;
import static com.mellearn.be.domain.quiz.listening.quiz.entity.QListeningQuiz.listeningQuiz;
import static com.mellearn.be.domain.quiz.listening.submit.entity.QListeningSubmit.listeningSubmit;
import static com.mellearn.be.domain.quiz.speaking.entity.QSpeakingSubmit.speakingSubmit;

@Slf4j
@Repository
public class QuizSubmitRepositoryImpl implements QuizSubmitRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public QuizSubmitRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
        this.em = em;
    }

    @Transactional(readOnly = true)
    public List<QuizSubmitDto> findSubmitWithPaging(long memberId, QuizType quizType, Long lastSeenId, int pageSize) {

        BooleanExpression idPredicate = lastSeenId != null
                ? quizSubmit.id.lt(lastSeenId)
                : quizSubmit.id.lt(Long.MAX_VALUE);

//         1. 페이징 쿼리로 submit id 가져오기
        List<Long> ids = queryFactory
                .select(quizSubmit.id)
                .from(quizSubmit)
                .where(
                        quizSubmit.quizList.quizType.eq(quizType),
                        quizSubmit.member.id.eq(memberId),
                        idPredicate
                )
                .orderBy(quizSubmit.createdTime.desc())
                .limit(pageSize)
                .fetch();

        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. submit 엔티티 가져오기 (quizList까지 fetch join)
        List<QuizSubmit> submits = queryFactory
                .selectFrom(quizSubmit)
                .join(quizSubmit.quizList, quizList).fetchJoin()
                .join(quizSubmit.submitAnswerList).fetchJoin()
                .where(quizSubmit.id.in(ids))
                .orderBy(quizSubmit.createdTime.desc())
                .fetch();


        // 3. DTO 변환
        return submits.stream()
                .map(QuizSubmitDto::new)
                .toList();

    }

    @Transactional(readOnly = true)
    public List<ListeningSubmitDto> findListeningSubmitWithPaging(
            long memberId,
            Long lastSeenId,
            int pageSize
    ) {

        BooleanExpression idPredicate = lastSeenId != null
                ? listeningSubmit.id.lt(lastSeenId)
                : null;

        // 1. 페이징용 submit id 목록
        List<Long> ids = queryFactory
                .select(listeningSubmit.id)
                .from(listeningSubmit)
                .where(listeningSubmit.member.id.eq(memberId), idPredicate)
                .orderBy(listeningSubmit.createdTime.desc())
                .limit(pageSize)
                .fetch();

        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. ListeningSubmit + ListeningQuiz fetch join
        List<ListeningSubmit> submits = queryFactory
                .selectFrom(listeningSubmit)
                .join(listeningSubmit.listeningQuiz, listeningQuiz).fetchJoin()
                .where(listeningSubmit.id.in(ids))
                .orderBy(listeningSubmit.createdTime.desc())
                .fetch();

        // 3. submitAnswerList 따로 초기화 (MultipleBagFetchException 방지)
        queryFactory
                .selectFrom(listeningSubmit)
                .join(listeningSubmit.submitAnswerList).fetchJoin()
                .where(listeningSubmit.id.in(ids))
                .fetch();

        // 4. DTO 변환
        return submits.stream()
                .map(ListeningSubmitDto::new)
                .toList();

    }


    @Transactional(readOnly = true)
    public List<SpeakingSubmitDto> findSpeakingSubmitWithPaging(long memberId, Long lastSeenId, int pageSize) {

        BooleanExpression idPredicate = lastSeenId != null
                ? speakingSubmit.id.lt(lastSeenId)
                : null;

        return queryFactory
                .select(Projections.constructor(SpeakingSubmitDto.class,
                        speakingSubmit.id,
                        speakingSubmit.musicId,
                        speakingSubmit.submit,
                        speakingSubmit.markedText,
                        speakingSubmit.score,
                        speakingSubmit.createdTime
                ))
                .from(speakingSubmit)
                .where(speakingSubmit.member.id.eq(memberId), idPredicate)
                .orderBy(speakingSubmit.createdTime.desc())
                .limit(pageSize)
                .fetch();
    }
}
