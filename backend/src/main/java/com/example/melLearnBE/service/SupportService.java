package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.model.MusicDto;
import com.example.melLearnBE.dto.request.LrcLyric;
import com.example.melLearnBE.dto.response.naverCloud.DetectLang;
import com.example.melLearnBE.enums.Language;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.model.Member;
import com.example.melLearnBE.model.Music;
import com.example.melLearnBE.openFeign.naverCloudClient.NaverCloudClient;
import com.example.melLearnBE.repository.MusicRepository;
import com.example.melLearnBE.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
                .orElseGet(() -> Music.builder()
                        .musicId(musicId)
                        .language(member.getLangType())
                        .checkCategoryAvailable(false)
                        .build());
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
