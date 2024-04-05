package com.example.melLearnBE.service;

import com.example.melLearnBE.dto.response.SupportQuizCategories;
import com.example.melLearnBE.enums.Language;
import com.example.melLearnBE.error.CustomException;
import com.example.melLearnBE.error.ErrorCode;
import com.example.melLearnBE.jwt.JwtTokenProvider;
import com.example.melLearnBE.model.Member;
import com.example.melLearnBE.openFeign.naverCloudClient.NaverCloudClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public SupportQuizCategories getSupportQuizCategory(String lyric, HttpServletRequest request) {
        SupportQuizCategories supportQuizCategories = new SupportQuizCategories();
        Member member = jwtTokenProvider.getMember(request).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));

        //가사의 언어 체크
        String langCode = naverCloudClient.detectLanguage(lyric);
        System.out.println(langCode);

        if(member.getLangType().getIso639Value().equals(langCode)) {
            supportQuizCategories.setListening(true);
            supportQuizCategories.setReading(true);
            supportQuizCategories.setGrammar(true);
            supportQuizCategories.setVocabulary(true);
        }

        // lrc 포맷인지 체크
        StringTokenizer tokenizer = new StringTokenizer(lyric, "\n");
        if(tokenizer.hasMoreTokens()) {
            String firstLyric = tokenizer.nextToken();
            boolean isLRCFormat = firstLyric.matches("(\\[\\d{2}:\\d{2}\\.\\d{2}\\])+.*");

            // LRC 포맷이 맞는 경우, 시간이 00:00.00인지 추가적으로 체크
            boolean isNotZeroTime = true; // 초기값은 true로 설정
            if (isLRCFormat) {
                // 정규 표현식을 사용하여 모든 시간 태그를 찾습니다.
                Pattern pattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2})\\]");
                Matcher matcher = pattern.matcher(firstLyric);

                while (matcher.find()) {
                    // 시간, 분, 초 추출.
                    int minutes = Integer.parseInt(matcher.group(1));
                    int seconds = Integer.parseInt(matcher.group(2));
                    int milliseconds = Integer.parseInt(matcher.group(3));

                    if (minutes == 0 && seconds == 0 && milliseconds == 0) {
                        isNotZeroTime = false;
                        break;
                    }
                }
            }

            if(isLRCFormat && isNotZeroTime) {
                supportQuizCategories.setSpeaking(true);
            }
        }

        return supportQuizCategories;
    }

}
