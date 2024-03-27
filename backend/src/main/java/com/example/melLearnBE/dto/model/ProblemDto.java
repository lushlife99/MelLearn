package com.example.melLearnBE.dto.model;

import com.example.melLearnBE.enums.ProbType;
import com.example.melLearnBE.model.Problem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDto {

    private Long id;
    private Long musicId;
    private ProbType probType;
    private String question;
    private List<String> selection;
    private int answer;
    private String comment;

    public ProblemDto(Problem problem) {
        this.id = problem.getId();
        this.musicId = problem.getMusicId();
        this.probType = problem.getProbType();
        this.question = problem.getQuestion();
        this.selection = problem.getSelection();
        this.answer = problem.getAnswer();
        this.comment = problem.getComment();
    }
}
