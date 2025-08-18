package com.mellearn.be.global.auth.service;

import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.entity.role.MemberRole;
import com.mellearn.be.domain.member.entity.role.MemberRoleId;
import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.member.repository.MemberRepository;
import com.mellearn.be.global.auth.dto.AuthRequest;
import com.mellearn.be.global.auth.jwt.dto.TokenInfo;
import com.mellearn.be.global.auth.jwt.service.JwtTokenProvider;
import com.mellearn.be.global.error.CustomException;
import com.mellearn.be.global.error.enums.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void join(AuthRequest joinRequest) {
        Optional<Member> user = memberRepository.findByMemberId(joinRequest.getMemberId());
        if (user.isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USERID);
        }

        Member member = Member.create(
                joinRequest.getMemberId(),
                encoder.encode(joinRequest.getPassword()),
                joinRequest.getName(),
                LearningLevel.Beginner,
                Language.ENGLISH,
                new ArrayList<>()
        );

        memberRepository.save(member);
        MemberRoleId roleId = new MemberRoleId(member.getId(), "ROLE_USER");
        MemberRole role = MemberRole.builder()
                .id(roleId)
                .member(member)
                .build();

        member.addRole(role);
    }

    @Transactional(readOnly = true)
    public TokenInfo login(AuthRequest loginRequest, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getMemberId(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.authenticate(authenticationToken);

        return jwtTokenProvider.generateToken(authentication, response);
    }

    public TokenInfo reIssueToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = "";

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken"))
                refreshToken = cookie.getValue();
        }
        return jwtTokenProvider.reissueToken(refreshToken, response);
    }
}
