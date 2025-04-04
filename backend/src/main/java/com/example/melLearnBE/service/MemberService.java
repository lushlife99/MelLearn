package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.model.MemberDto;
import com.example.melLearnBE.enums.Language;
import com.example.melLearnBE.enums.LearningLevel;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.jwt.JwtTokenProvider;
import com.example.melLearnBE.model.Member;
import com.example.melLearnBE.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public MemberDto getMemberProfile(HttpServletRequest request) {
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        return new MemberDto(member);
    }

    @Transactional
    public MemberDto updateMemberProfile(MemberDto memberDto, HttpServletRequest request) {
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));

        String name = StringUtils.hasText(memberDto.getName()) ? memberDto.getName() : member.getName();
        LearningLevel level = memberDto.getLevel() != null ? memberDto.getLevel() : member.getLevel();
        Language langType = StringUtils.hasText(memberDto.getLangType()) && Language.valueOfIso(memberDto.getLangType()) != null ? 
            Language.valueOfIso(memberDto.getLangType()) : member.getLangType();

        member.updateProfile(name, level, langType);
        return new MemberDto(member);
    }

    @Transactional
    public void updateSpotifyAccount(String accountId, HttpServletRequest request) {
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        if(StringUtils.hasText(accountId)) {
            member.updateSpotifyAccount(accountId);
        }
    }
}
