package com.example.melLearnBE.domain.quiz.comprehensive.dto;

import com.example.melLearnBE.domain.member.enums.LearningLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComprehensiveQuizSubmitDto {

    private String musicId;
    private LearningLevel level;
    private ComprehensiveQuizAnswerDto comprehensiveQuizAnswer;
}
