package com.example.melLearnBE.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ListeningQuizSubmit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listening_quiz_id")
    private ListeningQuiz listeningQuiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ElementCollection
    @CollectionTable(name = "listening_quiz_submit_answer", joinColumns = @JoinColumn(name = "listening_quiz_submit_id"))
    @Column(name = "answer")
    private List<String> submitAnswerList;

    private int score;

    private LocalDateTime createdTime;

    @Builder
    public ListeningQuizSubmit(ListeningQuiz listeningQuiz, Member member, List<String> submitAnswerList, int score) {
        this.listeningQuiz = listeningQuiz;
        this.member = member;
        this.submitAnswerList = submitAnswerList;
        this.score = score;
        this.createdTime = LocalDateTime.now();
    }

    public static ListeningQuizSubmit create(ListeningQuiz listeningQuiz, Member member, List<String> submitAnswerList) {
        return ListeningQuizSubmit.builder()
                .listeningQuiz(listeningQuiz)
                .member(member)
                .submitAnswerList(submitAnswerList)
                .score(calculateScore(submitAnswerList, listeningQuiz.getAnswerList()))
                .build();
    }

    private static int calculateScore(List<String> submitAnswers, List<String> correctAnswers) {
        int correctCount = 0;
        for (int i = 0; i < submitAnswers.size(); i++) {
            if (submitAnswers.get(i).equals(correctAnswers.get(i))) {
                correctCount++;
            }
        }
        return (int) ((double) correctCount / correctAnswers.size() * 100);
    }
} 