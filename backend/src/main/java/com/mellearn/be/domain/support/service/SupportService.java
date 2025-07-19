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
import com.mellearn.be.global.auth.jwt.service.JwtTokenProvider;
import com.mellearn.be.global.error.CustomException;
import com.mellearn.be.global.error.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final NaverCloudClient naverCloudClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final MusicRepository musicRepository;
    private static final int MAX_CHAR_SIZE = 100;
    private final MemberRepository memberRepository;

    public List<String> getSupportLang() {
        List<Language> langList = Arrays.stream(Language.values()).toList();
        ArrayList<String> isoList = new ArrayList<>();
        for (Language language : langList) {
            isoList.add(language.getIso639Value());
        }

        return isoList;
    }

    public MusicDto getSupportQuizCategory(String musicId, List<LrcLyric> lrcLyrics, String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        Music music = new Music();
        Optional<Music> optionalMusic = musicRepository.findByMusicId(musicId);
        if (optionalMusic.isPresent()) {
            music = optionalMusic.get();
            if(music.isCheckCategoryAvailable()) {
                return new MusicDto(optionalMusic.get());
            }
        }

        boolean listening = false;
        boolean speaking = false;
        boolean reading = false;
        boolean grammar = false;
        boolean vocabulary = false;

        String pureLyric = getPureLyric(lrcLyrics);

        if(StringUtils.hasText(pureLyric)) {
            String truncateLyric = truncateLyricByCharLimit(pureLyric);
            DetectLang language = naverCloudClient.detectLanguage(truncateLyric);
            if (member.getLangType().getIso639Value().equals(language.getLangCode())) {
                listening = true;
                reading = true;
                grammar = true;
                vocabulary = true;
                // lrc 포맷인지 체크
                if (lrcLyrics.size() != 0) {
                    LrcLyric lrcLyric = lrcLyrics.get(0);
                    if (lrcLyric.getDurMs() != 0) {
                        speaking = true;
                    }
                }
            }
        }

        if(optionalMusic.isPresent()) {
            music.setCheckCategoryAvailable(true);
            music.setListening(listening);
            music.setGrammar(grammar);
            music.setSpeaking(speaking);
            music.setReading(reading);
            music.setVocabulary(vocabulary);
        }

        else {
            music = Music.builder()
                    .musicId(musicId)
                    .grammar(grammar)
                    .language(member.getLangType())
                    .speaking(speaking)
                    .listening(listening)
                    .vocabulary(vocabulary)
                    .reading(reading)
                    .checkCategoryAvailable(true)
                    .build();
        }


        return new MusicDto(musicRepository.save(music));
    }

    private String truncateLyricByCharLimit(String lyric) {
        int charCount = lyric.length();

        if (charCount > MAX_CHAR_SIZE) {
            return lyric.substring(0, MAX_CHAR_SIZE);
        }
        return lyric;
    }


    private String getPureLyric(List<LrcLyric> lrcLyrics) {
        StringBuilder stringBuilder = new StringBuilder();

        for (LrcLyric lrcLyric : lrcLyrics) {
            stringBuilder.append(lrcLyric.getText());
        }

        return stringBuilder.toString();
    }

}
