package com.example.melLearnBE.dto.model;

import com.example.melLearnBE.model.QuizSubmit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmitDto {


    private Long id;
    private QuizListDto quizList;
    @Builder.Default
    private List<Integer> submitAnswerList = new ArrayList<>();

    public QuizSubmitDto(QuizSubmit quizSubmit) {
        this.id = quizSubmit.getId();
        this.quizList = new QuizListDto(quizSubmit.getQuizList());
        this.submitAnswerList = quizSubmit.getSubmitAnswerList();
    }
}
