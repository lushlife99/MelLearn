package com.example.melLearnBE.dto.model;

import com.example.melLearnBE.model.ListeningSubmit;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ListeningSubmitDto {

    private Long id;
    private List<String> answerList = new ArrayList<>();
    private List<String> submitAnswerList = new ArrayList<>();
    private double score;

    public ListeningSubmitDto(ListeningSubmit listeningSubmit) {
        this.id = listeningSubmit.getId();
        this.answerList = listeningSubmit.getListeningQuiz().getAnswerList();
        this.submitAnswerList = listeningSubmit.getSubmitAnswerList();
        this.score = listeningSubmit.getScore();
    }

    @QueryProjection
    public ListeningSubmitDto(Long id, List<String> answerList, List<String> submitAnswerList, double score) {
        this.id = id;
        this.answerList = answerList;
        this.submitAnswerList = submitAnswerList;
        this.score = score;
    }
}