package com.example.melLearnBE.domain.support.service;

import com.example.melLearnBE.domain.music.dto.MusicDto;
import com.example.melLearnBE.domain.music.dto.LrcLyric;
import com.example.melLearnBE.api.feign.naver.cloud.dto.DetectLang;
import com.example.melLearnBE.domain.member.enums.Language;
import com.example.melLearnBE.global.error.CustomException;
import com.example.melLearnBE.global.error.enums.ErrorCode;
import com.example.melLearnBE.domain.member.entity.Member;
import com.example.melLearnBE.domain.music.entity.Music;
import com.example.melLearnBE.api.feign.naver.cloud.NaverCloudClient;
import com.example.melLearnBE.domain.music.repository.MusicRepository;
import com.example.melLearnBE.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportService {

    private final NaverCloudClient naverCloudClient;
    private final MusicRepository musicRepository;
    private final MemberRepository memberRepository;
    private static final int MAX_CHAR_SIZE = 100;

    public List<String> getSupportLang() {
        List<Language> langList = Arrays.stream(Language.values()).toList();
        ArrayList<String> isoList = new ArrayList<>();
        for (Language language : langList) {
            isoList.add(language.getIso639Value());
        }

        return isoList;
    }

    public MusicDto getSupportQuizCategory(String musicId, List<LrcLyric> lrcLyrics, String memberId) {
        Member member = findMember(memberId);
        Music music = findOrCreateMusic(musicId, member);
        
        if (music.isCheckCategoryAvailable()) {
            return new MusicDto(music);
        }

        QuizCategory category = determineQuizCategory(lrcLyrics, member);
        updateMusicCategory(music, category);
        
        return new MusicDto(musicRepository.save(music));
    }

    private Member findMember(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> {
                    log.error("Member not found with id: {}", memberId);
                    return new CustomException(ErrorCode.BAD_REQUEST);
                });
    }

    private Music findOrCreateMusic(String musicId, Member member) {
        return musicRepository.findByMusicId(musicId)
                .orElseGet(() -> Music.create(
                    musicId,
                    member.getLangType(),
                    false
                ));
    }

    private QuizCategory determineQuizCategory(List<LrcLyric> lrcLyrics, Member member) {
        boolean basicCategories = false;
        boolean speaking = false;
        
        String pureLyric = getPureLyric(lrcLyrics);

        if (StringUtils.hasText(pureLyric)) {
            String truncateLyric = truncateLyricByCharLimit(pureLyric);
            DetectLang language = naverCloudClient.detectLanguage(truncateLyric);
            
            if (member.getLangType().getIso639Value().equals(language.getLangCode())) {
                basicCategories = true;
            }

            if (isLrcFormat(lrcLyrics)) {
                speaking = true;
            }
        }

        return new QuizCategory(basicCategories, speaking);
    }

    private boolean isLrcFormat(List<LrcLyric> lrcLyrics) {
        return !lrcLyrics.isEmpty() && lrcLyrics.get(0).getDurMs() != 0;
    }

    private void updateMusicCategory(Music music, QuizCategory category) {
        music.setCheckCategoryAvailable(true);
        music.setListening(category.basicCategories());
        music.setGrammar(category.basicCategories());
        music.setSpeaking(category.speaking());
        music.setReading(category.basicCategories());
        music.setVocabulary(category.basicCategories());
    }

    private String truncateLyricByCharLimit(String lyric) {
        return lyric.length() > MAX_CHAR_SIZE ? 
               lyric.substring(0, MAX_CHAR_SIZE) : 
               lyric;
    }

    private String getPureLyric(List<LrcLyric> lrcLyrics) {
        return lrcLyrics.stream()
                .map(LrcLyric::getText)
                .reduce("", String::concat);
    }

    private record QuizCategory(boolean basicCategories, boolean speaking) {}
}
