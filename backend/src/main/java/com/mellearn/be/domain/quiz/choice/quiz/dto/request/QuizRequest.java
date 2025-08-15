package com.mellearn.be.domain.quiz.choice.quiz.dto.request;

import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRequest {

    private String musicId;
    private QuizType quizType;
    private String lyric;
    private LearningLevel learningLevel;
    private Language language;

}
