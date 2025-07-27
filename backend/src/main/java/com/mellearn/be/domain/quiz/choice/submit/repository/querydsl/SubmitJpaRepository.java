package com.mellearn.be.domain.quiz.choice.submit.repository.querydsl;

import com.mellearn.be.domain.quiz.choice.submit.entity.QuizSubmit;
import com.mellearn.be.domain.quiz.listening.quiz.dto.ListeningQuizDto;
import com.mellearn.be.domain.quiz.listening.quiz.dto.QListeningQuizDto;
import com.mellearn.be.domain.quiz.listening.submit.dto.ListeningSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.listening.submit.dto.QListeningSubmitDto;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitDto;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.mellearn.be.domain.quiz.choice.quiz.entity.QQuizList.quizList;
import static com.mellearn.be.domain.quiz.choice.submit.entity.QQuizSubmit.quizSubmit;
import static com.mellearn.be.domain.quiz.listening.quiz.entity.QListeningQuiz.listeningQuiz;
import static com.mellearn.be.domain.quiz.listening.submit.entity.QListeningSubmit.listeningSubmit;
import static com.mellearn.be.domain.quiz.speaking.entity.QSpeakingSubmit.speakingSubmit;

@Repository
public class SubmitJpaRepository {

    private final JPAQueryFactory queryFactory;

    public SubmitJpaRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
    }

    /**
     * @ElementCollection 타입을 Join하는 쿼리는 Querydsl에서 지원하지 않음.
     * 그래서 한방 쿼리를 날리려면 Entity계층을 수정해야 함.
     * @ElementCollection 타입을 Entity로 등록하고 연관관계를 설정해주어야, fetchJoin을 사용할 수 있음.
     * <p>
     * 일단 지금은 트랜잭션을 사용해 Lazy-Fetch로 값을 불러오고, 나중에 수정하기.
     */
    @Transactional(readOnly = true)
    public Page<QuizSubmitDto> findSubmitWithPaging(long memberId, QuizType quizType, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<Long> ids = queryFactory
                .select(quizSubmit.id)
                .from(quizSubmit)
                .join(quizSubmit.quizList, quizList)
                .where(
                        quizSubmit.member.id.eq(memberId),
                        quizSubmit.quizList.quizType.eq(quizType)
                )
                .orderBy(quizSubmit.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<QuizSubmit> submits = queryFactory
                .selectFrom(quizSubmit)
                .join(quizSubmit.quizList, quizList).fetchJoin()
                .innerJoin(quizSubmit.submitAnswerList).fetchJoin()
                .where(quizSubmit.id.in(ids))
                .orderBy(quizSubmit.createdTime.desc())
                .fetch();

        List<QuizSubmitDto> results = submits.stream()
                .map(QuizSubmitDto::new)
                .toList();

        Long total = queryFactory
                .select(quizSubmit.count())
                .from(quizSubmit)
                .join(quizSubmit.quizList, quizList)
                .where(
                        quizSubmit.member.id.eq(memberId),
                        quizSubmit.quizList.quizType.eq(quizType)
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    @Transactional(readOnly = true)
    public Page<ListeningSubmitDto> findListeningSubmitWithPaging(long memberId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        StringPath submitAnswerList = Expressions.stringPath("submitAnswerList");
        StringPath answerListPath = Expressions.stringPath("answerList");

        List<ListeningSubmitDto> results = queryFactory
                .selectFrom(listeningSubmit)
                .transform(GroupBy.groupBy(listeningSubmit.id)
                        .list(Projections.constructor(ListeningSubmitDto.class,
                                listeningSubmit.id,
                                Projections.constructor(ListeningQuizDto.class,
                                        listeningSubmit.listeningQuiz.id,
                                        listeningSubmit.listeningQuiz.blankedText,
                                        Expressions.constant(Collections.emptyList()) // answerList 나중에 주입
                                ),
                                listeningSubmit.level,
                                GroupBy.list(submitAnswerList), // submitAnswerList is @ElementCollection
                                listeningSubmit.score,
                                listeningSubmit.createdTime
                        )));

        // 필요한 quizId들 수집
        List<Long> quizIds = results.stream()
                .map(dto -> dto.getListeningQuiz().getId())
                .distinct()
                .toList();

        // 2차 쿼리: answerList 가져오기

        Map<Long, List<String>> answerListMap = queryFactory
                .select(listeningQuiz.id, answerListPath)
                .from(listeningQuiz)
                .where(listeningQuiz.id.in(quizIds))
                .transform(GroupBy.groupBy(listeningQuiz.id).as(GroupBy.list(answerListPath)));

        // 주입
        for (ListeningSubmitDto dto : results) {
            Long quizId = dto.getListeningQuiz().getId();
            List<String> answerList = answerListMap.getOrDefault(quizId, Collections.emptyList());
            dto.getListeningQuiz().setAnswerList(answerList);
        }

        Long total = queryFactory
                .select(listeningSubmit.count())
                .from(listeningSubmit)
                .join(listeningSubmit.listeningQuiz, listeningQuiz)
                .where(listeningSubmit.member.id.eq(memberId))
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    @Transactional(readOnly = true)
    public Page<SpeakingSubmitDto> findSpeakingSubmitWithPaging(long memberId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<SpeakingSubmitDto> results = queryFactory
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
                .fetch();

        Long total = queryFactory
                .select(speakingSubmit.count())
                .from(speakingSubmit)
                .where(speakingSubmit.member.id.eq(memberId))
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }
}
