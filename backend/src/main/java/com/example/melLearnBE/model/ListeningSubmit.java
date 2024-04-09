package com.example.melLearnBE.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne
    private Member member;

    @ManyToOne
    private ListeningQuiz listeningQuiz;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> submitAnswerList = new ArrayList<>();
}
