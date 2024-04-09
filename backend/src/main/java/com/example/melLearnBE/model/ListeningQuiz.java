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
public class ListeningQuiz {

    @Id @GeneratedValue
    private Long id;
    private String musicId;
    private int level;
    @Column(columnDefinition="LONGTEXT")
    private String blankedText;

    @OneToMany(mappedBy = "listeningQuiz")
    private List<ListeningSubmit> submitList = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY) @Builder.Default
    private List<String> answerList = new ArrayList<>();
}
