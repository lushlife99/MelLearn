package com.mellearn.be.domain.member.repository;


import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.repository.querydsl.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

}
