package com.mellearn.be.domain.member.repository.querydsl;

import com.mellearn.be.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findByMemberId(String memberId);
}
