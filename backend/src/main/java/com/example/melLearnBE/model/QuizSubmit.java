package com.example.melLearnBE.model;

import com.example.melLearnBE.enums.LearningLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class QuizSubmit {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Member member;
    @ManyToOne
    private QuizList quizList;
    @ElementCollection(fetch = FetchType.LAZY) @Builder.Default
    private List<Integer> submitAnswerList = new ArrayList<>(4);
    private double score;
    private LocalDateTime createdTime;
}
