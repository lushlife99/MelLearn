package com.example.melLearnBE.dto.model;

import com.example.melLearnBE.model.ListeningSubmit;

import java.util.ArrayList;
import java.util.List;

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
}
