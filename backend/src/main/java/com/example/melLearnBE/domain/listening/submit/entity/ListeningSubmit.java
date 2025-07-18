package com.example.melLearnBE.domain.listening.submit.entity;

import com.example.melLearnBE.domain.listening.quiz.entity.ListeningQuiz;
import com.example.melLearnBE.domain.member.entity.Member;
import com.example.melLearnBE.domain.member.enums.LearningLevel;
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
    @GeneratedValue
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
}