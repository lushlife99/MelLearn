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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 25.08.08
 * login 처리를 spring security 모듈에서 처리하도록 변경
 * - 관련 테스트 주석처리
 */

@Transactional
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private AuthenticationManager authenticationManagerBuilder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthService authService;

    private AuthRequest testAuthRequest;
    private Member testMember;
    private TokenInfo testTokenInfo;
    private Authentication testAuthentication;

    @BeforeEach
    void setUp() {
        testAuthRequest = new AuthRequest();
        testAuthRequest.setMemberId("testMemberId");
        testAuthRequest.setPassword("testPassword");
        testAuthRequest.setName("Test User");

        testMember = Member.builder()
                .id(1L)
                .memberId("testMemberId")
                .name("Test User")
                .password("encodedPassword")
                .level(LearningLevel.Beginner)
                .langType(Language.ENGLISH)
                .build();

        testMember.addRole(new MemberRole(new MemberRoleId(1L, "ROLE_USER"), testMember));

        testTokenInfo = TokenInfo.builder()
                .grantType("Bearer")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        testAuthentication = new UsernamePasswordAuthenticationToken(
                testAuthRequest.getMemberId(),
                testAuthRequest.getPassword(),
                Collections.emptyList()
        );
    }

    @Test
    @DisplayName("회원가입 테스트 - 성공")
    void join_Success() {
        // given
        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.empty());
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any())).thenReturn(testMember);

        // when
        authService.join(testAuthRequest);

        // then
        verify(memberRepository).save(any());
    }

    @Test
    @DisplayName("회원가입 테스트 - 이미 존재하는 회원 ID")
    void join_AlreadyExistingMemberId_ShouldThrowException() {
        // given
        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.of(testMember));

        // when & then
        assertThrows(CustomException.class, () ->
                authService.join(testAuthRequest)
        );
    }

//    @Test
//    @DisplayName("로그인 테스트 - 성공")
//    void login_Success() {
//        // given
//        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.of(testMember));
//        when(encoder.matches(anyString(), anyString())).thenReturn(true);
//        when(authenticationManagerBuilder.authenticate(any())).thenReturn(testAuthentication);
//        when(jwtTokenProvider.generateToken(any(), any())).thenReturn(testTokenInfo);
//
//        // when
//        TokenInfo result = authService.login(testAuthRequest, response);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getAccessToken()).isEqualTo("accessToken");
//        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
//    }

//    @Test
//    @DisplayName("로그인 테스트 - 존재하지 않는 회원")
//    void login_NonExistingMember_ShouldThrowException() {
//        // given
//        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(CustomException.class, () ->
//                authService.login(testAuthRequest, response)
//        );
//    }

//    @Test
//    @DisplayName("로그인 테스트 - 비밀번호 불일치")
//    void login_MismatchedPassword_ShouldThrowException() {
//        // given
//        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.of(testMember));
//        when(encoder.matches(anyString(), anyString())).thenReturn(false);
//
//        // when & then
//        assertThrows(CustomException.class, () ->
//                authService.login(testAuthRequest, response)
//        );
//    }

    @Test
    @DisplayName("토큰 재발급 테스트 - 성공")
    void reIssueToken_Success() {
        // given
        Cookie[] cookies = new Cookie[]{new Cookie("refreshToken", "testRefreshToken")};
        when(request.getCookies()).thenReturn(cookies);
        when(jwtTokenProvider.reissueToken(anyString(), any())).thenReturn(testTokenInfo);

        // when
        TokenInfo result = authService.reIssueToken(request, response);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("토큰 재발급 테스트 - 리프레시 토큰 없음")
    void reIssueToken_NoRefreshToken_ShouldReturnNull() {
        // given
        when(request.getCookies()).thenReturn(new Cookie[0]);
        when(jwtTokenProvider.reissueToken(anyString(), any())).thenReturn(null);

        // when
        TokenInfo result = authService.reIssueToken(request, response);

        // then
        assertThat(result).isNull();
    }
} 