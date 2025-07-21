package com.mellearn.be.domain.quiz.listening.submit.dto.request;


import com.mellearn.be.domain.quiz.listening.quiz.entity.ListeningQuiz;
import com.mellearn.be.domain.quiz.listening.submit.entity.ListeningQuizSubmit;
import com.mellearn.be.domain.member.entity.Member;
import lombok.Getter;

import java.util.List;

@Getter
public class ListeningQuizSubmitRequest {
    private List<String> submitAnswerList;

    public ListeningQuizSubmit toListeningQuizSubmit(ListeningQuiz listeningQuiz, Member member) {
        return ListeningQuizSubmit.create(listeningQuiz, member, submitAnswerList);
    }
} 