package com.example.melLearnBE.repository.querydsl;

import com.example.melLearnBE.dto.model.ListeningSubmitDto;
import com.example.melLearnBE.dto.model.QListeningSubmitDto;
import com.example.melLearnBE.enums.QuizType;
import com.example.melLearnBE.model.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.melLearnBE.model.QListeningQuiz.listeningQuiz;
import static com.example.melLearnBE.model.QListeningSubmit.listeningSubmit;

@Repository
public class SubmitJpaRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public SubmitJpaRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Page<QuizSubmit> findSubmitWithPaging(long memberId, QuizType quizType, int pageNumber, int pageSize) {
        QQuizSubmit quizSubmit = QQuizSubmit.quizSubmit;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<QuizSubmit> results = queryFactory
                .selectFrom(quizSubmit)
                .where(quizSubmit.member.id.eq(memberId))
                .where(quizSubmit.quizList.quizType.eq(quizType))
                .orderBy(quizSubmit.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(quizSubmit)
                .where(quizSubmit.member.id.eq(memberId))
                .where(quizSubmit.quizList.quizType.eq(quizType))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);

    }

    public Page<ListeningSubmitDto> findListeningSubmitWithPaging(long memberId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        QListeningSubmit listeningSubmit = QListeningSubmit.listeningSubmit;

        List<ListeningSubmit> results = queryFactory
                .select(listeningSubmit)
                .from(listeningSubmit)
                .join(listeningSubmit.listeningQuiz, listeningQuiz)
                .where(listeningSubmit.member.id.eq(memberId))
                .orderBy(listeningSubmit.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(listeningSubmit)
                .from(listeningSubmit)
                .join(listeningSubmit.listeningQuiz, listeningQuiz)
                .where(listeningSubmit.member.id.eq(memberId))
                .fetchCount();

        List<ListeningSubmitDto> result = results.stream()
                .map(submit -> new ListeningSubmitDto(
                        submit.getId(),
                        submit.getListeningQuiz().getAnswerList(),
                        submit.getSubmitAnswerList(),
                        submit.getScore()))
                .collect(Collectors.toList());

        return new PageImpl<>(result, pageable, total);
    }

    public Page<SpeakingSubmit> findSpeakingSubmitWithPaging(long memberId, int pageNumber, int pageSize) {
        QSpeakingSubmit speakingSubmit = QSpeakingSubmit.speakingSubmit;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<SpeakingSubmit> results = queryFactory
                .selectFrom(speakingSubmit)
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
