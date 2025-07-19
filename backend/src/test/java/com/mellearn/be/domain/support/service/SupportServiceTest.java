package com.mellearn.be.domain.support.service;

import com.mellearn.be.api.feign.naver.cloud.NaverCloudClient;
import com.mellearn.be.api.feign.naver.cloud.dto.DetectLang;
import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.member.repository.MemberRepository;
import com.mellearn.be.domain.music.dto.LrcLyric;
import com.mellearn.be.domain.music.dto.MusicDto;
import com.mellearn.be.domain.music.entity.Music;
import com.mellearn.be.domain.music.repository.MusicRepository;
import com.mellearn.be.global.error.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportServiceTest {

    @Mock
    private NaverCloudClient naverCloudClient;

    @Mock
    private MusicRepository musicRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private SupportService supportService;

    private Member testMember;
    private Music testMusic;
    private List<LrcLyric> testLrcLyrics;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .memberId("testMemberId")
                .langType(Language.ENGLISH)
                .build();

        testMusic = Music.builder()
                .musicId("testMusicId")
                .language(Language.ENGLISH)
                .checkCategoryAvailable(false)
                .build();

        testLrcLyrics = Arrays.asList(
                new LrcLyric(0L, 1000L, "Hello"),
                new LrcLyric(1000L, 2000L, "World")
        );
    }

    @Test
    @DisplayName("지원 언어 목록 조회 테스트")
    void getSupportLang_ShouldReturnAllLanguageIsoCodes() {
        // when
        List<String> result = supportService.getSupportLang();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).contains(Language.ENGLISH.getIso639Value());
        assertThat(result).contains(Language.JAPANESE.getIso639Value());
    }

    @Test
    @DisplayName("기존 음악 카테고리 조회 테스트")
    void getSupportQuizCategory_ExistingMusic_ShouldReturnMusicDto() {
        // given
        testMusic.setCheckCategoryAvailable(true);
        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.of(testMember));
        when(musicRepository.findByMusicId(anyString())).thenReturn(Optional.of(testMusic));

        // when
        MusicDto result = supportService.getSupportQuizCategory("testMusicId", testLrcLyrics, "testMemberId");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMusicId()).isEqualTo("testMusicId");
        verify(musicRepository, never()).save(any());
    }

    @Test
    @DisplayName("새로운 음악 카테고리 생성 테스트")
    void getSupportQuizCategory_NewMusic_ShouldCreateAndReturnMusicDto() {
        // given
        DetectLang detectLang = new DetectLang();
        detectLang.setLangCode("en");
        
        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.of(testMember));
        when(musicRepository.findByMusicId(anyString())).thenReturn(Optional.empty());
        when(naverCloudClient.detectLanguage(anyString())).thenReturn(detectLang);
        when(musicRepository.save(any())).thenReturn(testMusic);

        // when
        MusicDto result = supportService.getSupportQuizCategory("testMusicId", testLrcLyrics, "testMemberId");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMusicId()).isEqualTo("testMusicId");
        verify(musicRepository).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID로 요청 시 예외 발생 테스트")
    void getSupportQuizCategory_InvalidMemberId_ShouldThrowException() {
        // given
        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () ->
                supportService.getSupportQuizCategory("testMusicId", testLrcLyrics, "invalidMemberId")
        );
    }

    @Test
    @DisplayName("빈 가사로 카테고리 생성 테스트")
    void getSupportQuizCategory_EmptyLyrics_ShouldCreateBasicMusic() {
        // given
        List<LrcLyric> emptyLyrics = Arrays.asList();
        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.of(testMember));
        when(musicRepository.findByMusicId(anyString())).thenReturn(Optional.empty());
        when(musicRepository.save(any())).thenReturn(testMusic);

        // when
        MusicDto result = supportService.getSupportQuizCategory("testMusicId", emptyLyrics, "testMemberId");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMusicId()).isEqualTo("testMusicId");
        verify(naverCloudClient, never()).detectLanguage(anyString());
    }
} 