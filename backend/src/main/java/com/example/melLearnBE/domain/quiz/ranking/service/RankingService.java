package com.example.melLearnBE.domain.quiz.ranking.service;

import com.example.melLearnBE.domain.quiz.ranking.dto.RankingDto;
import com.example.melLearnBE.domain.quiz.ranking.entity.Ranking;
import com.example.melLearnBE.domain.quiz.ranking.repository.RankingRepository;
import com.example.melLearnBE.global.error.CustomException;
import com.example.melLearnBE.global.error.enums.ErrorCode;
import com.example.melLearnBE.domain.quiz.speaking.entity.SpeakingSubmit;
import com.example.melLearnBE.domain.member.entity.Member;
import com.example.melLearnBE.domain.quiz.speaking.repository.SpeakingSubmitRepository;
import com.example.melLearnBE.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankingService {

    private final RankingRepository rankingRepository;
    private final MemberRepository memberRepository;
    private final SpeakingSubmitRepository speakingSubmitRepository;

    @Transactional
    public RankingDto updateRanking(String musicId, String memberId) {
        Member member = findMember(memberId);
        SpeakingSubmit speakingSubmit = findTopSpeakingSubmit(musicId, member);
        
        Ranking ranking = findOrCreateRanking(musicId);
        updateScoreList(ranking, memberId, speakingSubmit.getScore());
        
        return new RankingDto(rankingRepository.save(ranking));
    }

    @Transactional(readOnly = true)
    public RankingDto getRanking(String musicId) {
        return rankingRepository.findByMusicId(musicId)
                .map(RankingDto::new)
                .orElseGet(RankingDto::new);
    }

    private Member findMember(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> {
                    log.error("Member not found with id: {}", memberId);
                    return new CustomException(ErrorCode.BAD_REQUEST);
                });
    }

    private SpeakingSubmit findTopSpeakingSubmit(String musicId, Member member) {
        return speakingSubmitRepository.findTopByMusicIdAndMemberOrderByScoreDesc(musicId, member)
                .orElseThrow(() -> {
                    log.error("No speaking submit found for musicId: {} and member: {}", musicId, member.getMemberId());
                    return new CustomException(ErrorCode.BAD_REQUEST);
                });
    }

    private Ranking findOrCreateRanking(String musicId) {
        return rankingRepository.findByMusicId(musicId)
                .orElseGet(() -> Ranking.builder()
                        .score_list(new HashMap<>())
                        .musicId(musicId)
                        .build());
    }

    private void updateScoreList(Ranking ranking, String memberId, double score) {
        Map<String, Double> scoreList = ranking.getScore_list();
        scoreList.put(memberId, score);
    }
}
