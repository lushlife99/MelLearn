package com.example.melLearnBE.dto.request;

import com.example.melLearnBE.model.ListeningQuiz;
import com.example.melLearnBE.model.ListeningQuizSubmit;
import com.example.melLearnBE.model.Member;
import lombok.Getter;

import java.util.List;

@Getter
public class ListeningQuizSubmitRequest {
    private List<String> submitAnswerList;

    public ListeningQuizSubmit toListeningQuizSubmit(ListeningQuiz listeningQuiz, Member member) {
        return ListeningQuizSubmit.create(listeningQuiz, member, submitAnswerList);
    }
} 