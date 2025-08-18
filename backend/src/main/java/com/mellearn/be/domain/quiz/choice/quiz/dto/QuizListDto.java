package com.mellearn.be.domain.quiz.choice.quiz.dto;

import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.entity.Quiz;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizListDto {

    private Long id;
    private QuizType quizType;
    private List<QuizDto> quizzes;
    @Column(unique = true)
    private String musicId;
    private int level;
    private LocalDateTime createdTime;

    public QuizListDto(QuizList quizList) {
        this.id = quizList.getId();
        quizzes = new ArrayList<>();
        for (Quiz quiz : quizList.getQuizzes()) {
            quizzes.add(new QuizDto(quiz));
        }
        this.musicId = quizList.getMusicId();
        this.level = quizList.getLevel().getValue();
        this.createdTime = quizList.getCreatedTime();
    }

    @QueryProjection
    public QuizListDto(Long id, QuizType quizType, LearningLevel level, String musicId) {
        this.id = id;
        this.quizType = quizType;
        this.level = level.getValue();
        this.musicId = musicId;
        this.quizzes = new ArrayList<>();
    }
}
