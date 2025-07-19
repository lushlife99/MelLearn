package com.mellearn.be.domain.quiz.choice.submit.dto;

import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.submit.entity.QuizSubmit;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class QuizSubmitDto {


    private Long id;
    private QuizListDto quizList;
    private List<Integer> submitAnswerList;
    private double score;
    private LocalDateTime createdTime;

    public QuizSubmitDto(QuizSubmit quizSubmit) {
        this.id = quizSubmit.getId();
        this.quizList = new QuizListDto(quizSubmit.getQuizList());
        this.submitAnswerList = quizSubmit.getSubmitAnswerList();
        this.score = quizSubmit.getScore();
        this.createdTime = quizSubmit.getCreatedTime();
    }

    @QueryProjection
    public QuizSubmitDto(Long id, QuizListDto quizList, List<Integer> submitAnswerList, double score, LocalDateTime createdTime) {
        this.id = id;
        this.quizList = quizList;
        if (submitAnswerList != null) {
            this.submitAnswerList = List.copyOf(submitAnswerList);
        }
        this.score = score;
        this.createdTime = createdTime;
    }
}
