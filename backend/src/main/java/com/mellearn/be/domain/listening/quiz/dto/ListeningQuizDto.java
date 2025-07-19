package com.mellearn.be.domain.listening.quiz.dto;

import com.mellearn.be.domain.listening.quiz.entity.ListeningQuiz;
import lombok.Data;

import java.util.List;

@Data
public class ListeningQuizDto {

    private Long id;
    private String blankedText;
    private List<String> answerList;

    public ListeningQuizDto(ListeningQuiz listeningQuiz) {
        this.id = listeningQuiz.getId();
        this.blankedText = listeningQuiz.getBlankedText();
        this.answerList = List.copyOf(listeningQuiz.getAnswerList());
    }
}
