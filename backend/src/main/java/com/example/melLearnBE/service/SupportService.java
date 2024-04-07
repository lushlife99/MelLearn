package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.request.LrcLyric;
import com.example.melLearnBE.dto.response.SupportQuizCategories;
import com.example.melLearnBE.dto.response.naverCloud.DetectLang;
import com.example.melLearnBE.enums.Language;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.jwt.JwtTokenProvider;
import com.example.melLearnBE.model.Member;
import com.example.melLearnBE.openFeign.naverCloudClient.NaverCloudClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final NaverCloudClient naverCloudClient;
    private final JwtTokenProvider jwtTokenProvider;

    public List<String> getSupportLang() {
        List<Language> langList = Arrays.stream(Language.values()).toList();
        ArrayList<String> isoList = new ArrayList<>();
        for (Language language : langList) {
            isoList.add(language.getIso639Value());
        }

        return isoList;
    }

    public SupportQuizCategories getSupportQuizCategory(List<LrcLyric> lrcLyrics, HttpServletRequest request) {
        SupportQuizCategories supportQuizCategories = new SupportQuizCategories();
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));

        String pureLyric = getPureLyric(lrcLyrics);
        //가사의 언어 체크
        if(!StringUtils.hasText(pureLyric))
            return supportQuizCategories;

        DetectLang detectLang = naverCloudClient.detectLanguage(pureLyric);
        System.out.println(detectLang.getLangCode());
        System.out.println(member.getLangType().getIso639Value());
        if(member.getLangType().getIso639Value().equals(detectLang.getLangCode())) {
            supportQuizCategories.setListening(true);
            supportQuizCategories.setReading(true);
            supportQuizCategories.setGrammar(true);
            supportQuizCategories.setVocabulary(true);
        } else {
            return supportQuizCategories;
        }


        // lrc 포맷인지 체크
        if(lrcLyrics.size() != 0) {
            LrcLyric lrcLyric = lrcLyrics.get(0);
            if (lrcLyric.getDurMs() != 0) {
                supportQuizCategories.setSpeaking(true);
            }
        }

        return supportQuizCategories;
    }

    private String getPureLyric(List<LrcLyric> lrcLyrics) {
        StringBuilder stringBuilder = new StringBuilder();

        for (LrcLyric lrcLyric : lrcLyrics) {
            stringBuilder.append(lrcLyric.getText());
        }

        return stringBuilder.toString();
    }

}
