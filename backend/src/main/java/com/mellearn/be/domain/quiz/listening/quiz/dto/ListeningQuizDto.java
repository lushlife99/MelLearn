package com.mellearn.be.domain.quiz.listening.quiz.dto;

import com.mellearn.be.domain.quiz.listening.quiz.entity.ListeningQuiz;
import com.querydsl.core.annotations.QueryProjection;
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

    @QueryProjection
    public ListeningQuizDto(Long id, String blankedText, List<String> answerList) {
        this.id = id;
        this.blankedText = blankedText;
        this.answerList = answerList;
    }
}
