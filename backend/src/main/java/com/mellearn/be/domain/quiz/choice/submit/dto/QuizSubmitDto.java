package com.mellearn.be.domain.quiz.choice.submit.dto;

import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.dto.QuizListDto;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.mellearn.be.domain.quiz.choice.submit.entity.QuizSubmit;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    public QuizSubmitDto(Long id, Long quizListId, QuizType quizType, LearningLevel level, String musicId,
                         String submitAnswerList, Integer score, LocalDateTime createdTime) {
        this.id = id;
        this.quizList = new QuizListDto(quizListId, quizType, level, musicId);
        this.submitAnswerList = submitAnswerList == null ? new java.util.ArrayList<>() :
                java.util.Arrays.stream(submitAnswerList.split(","))
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .collect(java.util.stream.Collectors.toList());
        this.score = score != null ? score : 0;
        this.createdTime = createdTime;
    }

}
