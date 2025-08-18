package com.mellearn.be.domain.member.repository.querydsl.impl;

import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.entity.QMember;
import com.mellearn.be.domain.member.repository.querydsl.MemberRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.mellearn.be.domain.member.entity.QMember.member;

@Repository
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Member> findByMemberId(String memberId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(member)
                .leftJoin(member.roles).fetchJoin()
                .where(member.memberId.eq(memberId))
                .fetchOne()
        );
    }
}
