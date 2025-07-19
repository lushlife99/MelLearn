package com.mellearn.be.domain.quiz.comprehensive.dto;

import com.mellearn.be.domain.member.enums.LearningLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComprehensiveQuizSubmitDto {

    private String musicId;
    private LearningLevel level;
    private ComprehensiveQuizAnswerDto comprehensiveQuizAnswer;
}
