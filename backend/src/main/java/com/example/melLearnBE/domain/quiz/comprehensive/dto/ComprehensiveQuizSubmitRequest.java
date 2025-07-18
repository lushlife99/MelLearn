package com.example.melLearnBE.domain.quiz.comprehensive.dto;

import com.example.melLearnBE.domain.music.dto.LrcLyric;
import lombok.Data;

import java.util.List;

@Data
public class ComprehensiveQuizSubmitRequest {

    private String musicId;
    private List<Integer> readingSubmit;
    private List<Integer> vocabularySubmit;
    private List<Integer> grammarSubmit;
    private List<String> listeningSubmit;
    private List<LrcLyric> lrcLyricList;
}