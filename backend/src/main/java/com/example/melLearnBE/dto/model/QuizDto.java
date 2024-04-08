package com.example.melLearnBE.dto.model;

import com.example.melLearnBE.model.Quiz;
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
    private List<String> selection;
    private int answer;
    private String comment;

    public QuizDto(Quiz quiz) {
        this.id = quiz.getId();
        this.question = quiz.getQuestion();
        this.selection = quiz.getSelection();
        this.answer = quiz.getAnswer();
        this.comment = quiz.getComment();
    }
}
