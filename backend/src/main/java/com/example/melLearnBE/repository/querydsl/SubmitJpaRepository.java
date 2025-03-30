package com.example.melLearnBE.repository.querydsl;

import com.example.melLearnBE.dto.model.*;
import com.example.melLearnBE.enums.QuizType;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import static com.example.melLearnBE.model.QListeningQuiz.listeningQuiz;
import static com.example.melLearnBE.model.QListeningSubmit.listeningSubmit;
import static com.example.melLearnBE.model.QQuizSubmit.quizSubmit;
import static com.example.melLearnBE.model.QSpeakingSubmit.speakingSubmit;

@Repository
public class SubmitJpaRepository {

    private final JPAQueryFactory queryFactory;

    public SubmitJpaRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Transactional(readOnly = true)
    public Page<QuizSubmitDto> findSubmit(long memberId, QuizType quizType, int pageNumber, int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        QueryResults<QuizSubmitDto> results = queryFactory
                .select(Projections.constructor(QuizSubmitDto.class,
                        quizSubmit.id,
                        Projections.constructor(QuizListDto.class,
                                quizSubmit.quizList.id,
                                quizSubmit.quizList.quizType,
                                quizSubmit.quizList.level
                        ),
                        quizSubmit.submitAnswerList,
                        quizSubmit.score,
                        quizSubmit.createdTime
                ))
                .from(quizSubmit)
                .where(quizSubmit.member.id.eq(memberId),
                        quizSubmit.quizList.quizType.eq(quizType))
                .orderBy(quizSubmit.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());

    }

    @Transactional(readOnly = true)
    public Page<ListeningSubmitDto> findListeningSubmit(long memberId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        QueryResults<ListeningSubmitDto> results = queryFactory
                .select(Projections.constructor(ListeningSubmitDto.class,
                        listeningSubmit.id,
                        Projections.constructor(ListeningQuizDto.class,
                                listeningSubmit.listeningQuiz.id,
                                listeningSubmit.listeningQuiz.musicId,
                                listeningSubmit.listeningQuiz.level
                        ),
                        listeningSubmit.level,
                        listeningSubmit.submitAnswerList,
                        listeningSubmit.score,
                        listeningSubmit.createdTime
                ))
                .from(listeningSubmit)
                .join(listeningSubmit.listeningQuiz, listeningQuiz).fetchJoin()
                .where(listeningSubmit.member.id.eq(memberId))
                .orderBy(listeningSubmit.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    @Transactional(readOnly = true)
    public Page<SpeakingSubmitDto> findSpeakingSubmit(long memberId, int pageNumber, int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        QueryResults<SpeakingSubmitDto> results = queryFactory
                .select(Projections.constructor(SpeakingSubmitDto.class,
                        speakingSubmit.id,
                        speakingSubmit.musicId,
                        speakingSubmit.submit,
                        speakingSubmit.markedText,
                        speakingSubmit.score,
                        speakingSubmit.createdTime
                ))
                .from(speakingSubmit)
                .where(speakingSubmit.member.id.eq(memberId))
                .orderBy(speakingSubmit.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }


}
