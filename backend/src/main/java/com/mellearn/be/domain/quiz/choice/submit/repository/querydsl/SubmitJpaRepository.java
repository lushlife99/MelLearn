package com.mellearn.be.domain.quiz.choice.submit.repository.querydsl;

import com.mellearn.be.domain.quiz.listening.quiz.dto.ListeningQuizDto;
import com.mellearn.be.domain.quiz.listening.submit.dto.ListeningSubmitDto;
import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.listening.submit.entity.QListeningSubmit;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitDto;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mellearn.be.domain.quiz.choice.quiz.entity.QQuizList.quizList;
import static com.mellearn.be.domain.quiz.choice.submit.entity.QQuizSubmit.quizSubmit;
import static com.mellearn.be.domain.quiz.listening.quiz.entity.QListeningQuiz.listeningQuiz;
import static com.mellearn.be.domain.quiz.listening.submit.entity.QListeningSubmit.listeningSubmit;
import static com.mellearn.be.domain.quiz.speaking.entity.QSpeakingSubmit.speakingSubmit;


@Repository
public class SubmitJpaRepository {

    private final JPAQueryFactory queryFactory;

    public SubmitJpaRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * @ElementCollection 타입을 Join하는 쿼리는 Querydsl에서 지원하지 않음.
     * 그래서 한방 쿼리를 날리려면 Entity계층을 수정해야 함.
     * @ElementCollection 타입을 Entity로 등록하고 연관관계를 설정해주어야, fetchJoin을 사용할 수 있음.
     *
     * 일단 지금은 트랜잭션을 사용해 Lazy-Fetch로 값을 불러오고, 나중에 수정하기.
     */
    @Transactional(readOnly = true)
    public Page<QuizSubmitDto> findSubmitWithPaging(long memberId, QuizType quizType, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<QuizSubmitDto> results = queryFactory
                .select(Projections.constructor(QuizSubmitDto.class,
                        quizSubmit.id,
                        Projections.constructor(QuizListDto.class,
                                quizSubmit.quizList.id,
                                quizSubmit.quizList.quizType,
                                quizSubmit.quizList.level,
                                quizSubmit.quizList.musicId
                        ),
                        quizSubmit.submitAnswerList,
                        quizSubmit.score,
                        quizSubmit.createdTime
                ))
                .from(quizSubmit)
                .join(quizSubmit.quizList, quizList).fetchJoin()
                .where(quizSubmit.member.id.eq(memberId),
                        quizSubmit.quizList.quizType.eq(quizType))
                .orderBy(quizSubmit.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(quizSubmit)
                .where(quizSubmit.member.id.eq(memberId),
                        quizSubmit.quizList.quizType.eq(quizType))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Transactional(readOnly = true)
    public Page<ListeningSubmitDto> findListeningSubmitWithPaging(long memberId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<ListeningSubmitDto> results = queryFactory
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
                .fetch();

        long total = queryFactory
                .selectFrom(listeningSubmit)
                .where(listeningSubmit.member.id.eq(memberId))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
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

        long total = queryFactory
                .selectFrom(speakingSubmit)
                .where(speakingSubmit.member.id.eq(memberId))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
