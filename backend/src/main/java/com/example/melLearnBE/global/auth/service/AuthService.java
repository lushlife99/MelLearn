package com.example.melLearnBE.global.auth.service;

import com.example.melLearnBE.global.auth.dto.AuthRequest;
import com.example.melLearnBE.global.auth.jwt.dto.TokenInfo;
import com.example.melLearnBE.domain.member.enums.Language;
import com.example.melLearnBE.domain.member.enums.LearningLevel;
import com.example.melLearnBE.global.error.CustomException;
import com.example.melLearnBE.global.error.enums.ErrorCode;
import com.example.melLearnBE.global.auth.jwt.service.JwtTokenProvider;
import com.example.melLearnBE.domain.member.entity.Member;
import com.example.melLearnBE.domain.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public void join(AuthRequest joinRequest) {
        Optional<Member> user = memberRepository.findByMemberId(joinRequest.getMemberId());
        if(user.isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USERID);
        }

        Member joinMember = Member.create(
            joinRequest.getMemberId(),
            encoder.encode(joinRequest.getPassword()),
            joinRequest.getName(),
            LearningLevel.Beginner,
            Language.ENGLISH,
            Collections.singletonList("ROLE_USER")
        );
        
        memberRepository.save(joinMember);
    }

    public TokenInfo login(AuthRequest loginRequest, HttpServletResponse response) {
        Member member = memberRepository.findByMemberId(loginRequest.getMemberId())
            .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
            
        if(!encoder.matches(loginRequest.getPassword(), member.getPassword())){
            throw new CustomException(ErrorCode.MISMATCHED_PASSWORD);
        }
        
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginRequest.getMemberId(), 
            loginRequest.getPassword()
        );
        
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
