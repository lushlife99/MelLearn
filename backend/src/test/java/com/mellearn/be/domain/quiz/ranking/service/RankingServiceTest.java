package com.mellearn.be.domain.quiz.ranking.service;

import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.repository.MemberRepository;
import com.mellearn.be.domain.quiz.ranking.dto.RankingDto;
import com.mellearn.be.domain.quiz.ranking.entity.Ranking;
import com.mellearn.be.domain.quiz.ranking.repository.RankingRepository;
import com.mellearn.be.domain.quiz.ranking.service.RankingService;
import com.mellearn.be.domain.quiz.speaking.entity.SpeakingSubmit;
import com.mellearn.be.domain.quiz.speaking.repository.SpeakingSubmitRepository;
import com.mellearn.be.global.error.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private RankingRepository rankingRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SpeakingSubmitRepository speakingSubmitRepository;

    @InjectMocks
    private RankingService rankingService;

    private Member testMember;
    private SpeakingSubmit testSpeakingSubmit;
    private Ranking testRanking;
    private Map<String, Double> testScoreList;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .memberId("testMemberId")
                .build();

        testSpeakingSubmit = SpeakingSubmit.builder()
                .member(testMember)
                .musicId("testMusicId")
                .score(85.5)
                .build();

        testScoreList = new HashMap<>();
        testScoreList.put("testMemberId", 85.5);

        testRanking = Ranking.builder()
                .musicId("testMusicId")
                .score_list(testScoreList)
                .build();
    }

    @Test
    @DisplayName("랭킹 업데이트 테스트 - 새로운 랭킹 생성")
    void updateRanking_NewRanking_ShouldCreateAndReturnRankingDto() {
        // given
        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.of(testMember));
        when(speakingSubmitRepository.findTopByMusicIdAndMemberOrderByScoreDesc(anyString(), any()))
                .thenReturn(Optional.of(testSpeakingSubmit));
        when(rankingRepository.findByMusicId(anyString())).thenReturn(Optional.empty());
        when(rankingRepository.save(any())).thenReturn(testRanking);

        // when
        RankingDto result = rankingService.updateRanking("testMusicId", "testMemberId");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMusicId()).isEqualTo("testMusicId");
        assertThat(result.getScore_list()).containsEntry("testMemberId", 85.5);
    }

    @Test
    @DisplayName("랭킹 업데이트 테스트 - 기존 랭킹 업데이트")
    void updateRanking_ExistingRanking_ShouldUpdateAndReturnRankingDto() {
        // given
        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.of(testMember));
        when(speakingSubmitRepository.findTopByMusicIdAndMemberOrderByScoreDesc(anyString(), any()))
                .thenReturn(Optional.of(testSpeakingSubmit));
        when(rankingRepository.findByMusicId(anyString())).thenReturn(Optional.of(testRanking));
        when(rankingRepository.save(any())).thenReturn(testRanking);

        // when
        RankingDto result = rankingService.updateRanking("testMusicId", "testMemberId");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMusicId()).isEqualTo("testMusicId");
        assertThat(result.getScore_list()).containsEntry("testMemberId", 85.5);
    }

    @Test
    @DisplayName("랭킹 조회 테스트 - 존재하는 랭킹")
    void getRanking_ExistingRanking_ShouldReturnRankingDto() {
        // given
        when(rankingRepository.findByMusicId(anyString())).thenReturn(Optional.of(testRanking));

        // when
        RankingDto result = rankingService.getRanking("testMusicId");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMusicId()).isEqualTo("testMusicId");
        assertThat(result.getScore_list()).containsEntry("testMemberId", 85.5);
    }

    @Test
    @DisplayName("랭킹 조회 테스트 - 존재하지 않는 랭킹")
    void getRanking_NonExistingRanking_ShouldReturnEmptyRankingDto() {
        // given
        when(rankingRepository.findByMusicId(anyString())).thenReturn(Optional.empty());

        // when
        RankingDto result = rankingService.getRanking("testMusicId");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMusicId()).isNull();
        assertThat(result.getScore_list()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 랭킹 업데이트 시도 시 예외 발생")
    void updateRanking_InvalidMember_ShouldThrowException() {
        // given
        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () ->
                rankingService.updateRanking("testMusicId", "invalidMemberId")
        );
    }

    @Test
    @DisplayName("존재하지 않는 말하기 제출로 랭킹 업데이트 시도 시 예외 발생")
    void updateRanking_InvalidSpeakingSubmit_ShouldThrowException() {
        // given
        when(memberRepository.findByMemberId(anyString())).thenReturn(Optional.of(testMember));
        when(speakingSubmitRepository.findTopByMusicIdAndMemberOrderByScoreDesc(anyString(), any()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () ->
                rankingService.updateRanking("testMusicId", "testMemberId")
        );
    }
} 