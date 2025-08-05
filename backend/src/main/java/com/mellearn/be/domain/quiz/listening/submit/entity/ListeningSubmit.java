package com.mellearn.be.domain.quiz.listening.submit.entity;

import com.mellearn.be.domain.quiz.listening.quiz.entity.ListeningQuiz;
import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.enums.LearningLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class ListeningSubmit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(value = EnumType.ORDINAL)
    private LearningLevel level;

    @ManyToOne
    private Member member;

    @ManyToOne
    private ListeningQuiz listeningQuiz;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private List<String> submitAnswerList = new ArrayList<>();

    private double score;
    @CreationTimestamp
    private LocalDateTime createdTime;

    public static ListeningSubmit create(ListeningQuiz listeningQuiz, Member member, List<String> submitAnswerList) {
        return ListeningSubmit.builder()
                .listeningQuiz(listeningQuiz)
                .member(member)
                .level(member.getLevel())
                .submitAnswerList(submitAnswerList)
                .score(calculateScore(submitAnswerList, listeningQuiz.getAnswerList()))
                .createdTime(LocalDateTime.now())
                .build();
    }

    private static double calculateScore(List<String> submitAnswers, List<String> correctAnswers) {
        double correctCount = 0;
        for (int i = 0; i < submitAnswers.size(); i++) {
            if (submitAnswers.get(i).equals(correctAnswers.get(i))) {
                correctCount++;
            }
        }
        return correctCount / correctAnswers.size() * 100;
    }
}