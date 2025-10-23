package com.mellearn.be.domain.quiz.choice.submit.dto;


import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MusicQuizSubmit {

    private QuizType quizType;
    private long quizListId;
    private int score;
    private LocalDateTime createdAt;
    private String memberName;

    @QueryProjection
    public MusicQuizSubmit(QuizType quizType, long quizListId, int score, LocalDateTime createdAt, String memberName) {
        this.quizType = quizType;
        this.quizListId = quizListId;
        this.score = score;
        this.createdAt = createdAt;
        this.memberName = memberName;
    }
}
