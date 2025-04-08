package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.model.MemberDto;
import com.example.melLearnBE.enums.Language;
import com.example.melLearnBE.enums.LearningLevel;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.jwt.JwtTokenProvider;
import com.example.melLearnBE.model.Member;
import com.example.melLearnBE.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;
    private MemberDto testMemberDto;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .memberId("testMemberId")
                .name("Test User")
                .level(LearningLevel.Beginner)
                .langType(Language.ENGLISH)
                .spotifyAccountId("spotify123")
                .build();

        testMemberDto = new MemberDto(testMember);
    }

    @Test
    @DisplayName("회원 프로필 조회 테스트")
    void getMemberProfile_ShouldReturnMemberDto() {
        // given
        when(jwtTokenProvider.getMember(any())).thenReturn(Optional.of(testMember));

        // when
        MemberDto result = memberService.getMemberProfile(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo("testMemberId");
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getLevel()).isEqualTo(LearningLevel.Beginner);
        assertThat(result.getLangType()).isEqualTo(Language.ENGLISH.getIso639Value());
    }

    @Test
    @DisplayName("회원 프로필 조회 실패 테스트 - 인증되지 않은 요청")
    void getMemberProfile_UnauthorizedRequest_ShouldThrowException() {
        // given
        when(jwtTokenProvider.getMember(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () ->
                memberService.getMemberProfile(request)
        );
    }

    @Test
    @DisplayName("회원 프로필 업데이트 테스트 - 모든 필드 업데이트")
    void updateMemberProfile_AllFields_ShouldUpdateAndReturnMemberDto() {
        // given
        MemberDto updateDto = new MemberDto();
        updateDto.setName("Updated Name");
        updateDto.setLevel(LearningLevel.Intermediate);
        updateDto.setLangType(Language.JAPANESE.getIso639Value());

        when(jwtTokenProvider.getMember(any())).thenReturn(Optional.of(testMember));

        // when
        MemberDto result = memberService.updateMemberProfile(updateDto, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getLevel()).isEqualTo(LearningLevel.Intermediate);
        assertThat(result.getLangType()).isEqualTo(Language.JAPANESE.getIso639Value());
    }

    @Test
    @DisplayName("회원 프로필 업데이트 테스트 - 부분 필드 업데이트")
    void updateMemberProfile_PartialFields_ShouldUpdateAndReturnMemberDto() {
        // given
        MemberDto updateDto = new MemberDto();
        updateDto.setName("Updated Name");

        when(jwtTokenProvider.getMember(any())).thenReturn(Optional.of(testMember));

        // when
        MemberDto result = memberService.updateMemberProfile(updateDto, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getLevel()).isEqualTo(LearningLevel.Beginner); // 기존 값 유지
        assertThat(result.getLangType()).isEqualTo(Language.ENGLISH.getIso639Value()); // 기존 값 유지
    }

    @Test
    @DisplayName("Spotify 계정 업데이트 테스트")
    void updateSpotifyAccount_ShouldUpdateAccountId() {
        // given
        String newAccountId = "newSpotify123";
        when(jwtTokenProvider.getMember(any())).thenReturn(Optional.of(testMember));

        // when
        memberService.updateSpotifyAccount(newAccountId, request);

        // then
        assertThat(testMember.getSpotifyAccountId()).isEqualTo(newAccountId);
    }

    @Test
    @DisplayName("Spotify 계정 업데이트 실패 테스트 - 빈 계정 ID")
    void updateSpotifyAccount_EmptyAccountId_ShouldNotUpdate() {
        // given
        String originalAccountId = testMember.getSpotifyAccountId();
        when(jwtTokenProvider.getMember(any())).thenReturn(Optional.of(testMember));

        // when
        memberService.updateSpotifyAccount("", request);

        // then
        assertThat(testMember.getSpotifyAccountId()).isEqualTo(originalAccountId);
    }
} 