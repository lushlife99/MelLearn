package com.mellearn.be.domain.quiz.choice.quiz.dto;

import com.mellearn.be.domain.quiz.choice.quiz.entity.Quiz;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDto {

    private Long id;
    private String question;
    private List<String> optionList;
    private int answer;
    private String comment;

    public QuizDto(Quiz quiz) {
        this.id = quiz.getId();
        this.question = quiz.getQuestion();
        this.optionList = List.copyOf(quiz.getOptionList());
        this.answer = quiz.getAnswer();
        this.comment = quiz.getComment();
    }

}
