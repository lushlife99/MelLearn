package com.mellearn.be.domain.member.service;

import com.mellearn.be.domain.member.dto.MemberDto;
import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.member.repository.MemberRepository;
import com.mellearn.be.global.error.CustomException;
import com.mellearn.be.global.error.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberDto getMemberProfile(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        return new MemberDto(member);
    }

    @Transactional
    public MemberDto updateMemberProfile(MemberDto memberDto, String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));

        String name = StringUtils.hasText(memberDto.getName()) ? memberDto.getName() : member.getName();
        LearningLevel level = memberDto.getLevel() != null ? memberDto.getLevel() : member.getLevel();
        Language langType = StringUtils.hasText(memberDto.getLangType()) && Language.valueOfIso(memberDto.getLangType()) != null ?
            Language.valueOfIso(memberDto.getLangType()) : member.getLangType();

        member.updateProfile(name, level, langType);
        return new MemberDto(member);
    }

    @Transactional
    public void updateSpotifyAccount(String accountId, String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));

        if(StringUtils.hasText(accountId)) {
            member.updateSpotifyAccount(accountId);
        }
    }
}
