package com.example.melLearnBE.model;

import com.example.melLearnBE.enums.LearningLevel;
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
@Table(name = "listening_quiz", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"musicId", "level"})
})
public class ListeningQuiz {

    @Id @GeneratedValue
    private Long id;
    private String musicId;
    @Enumerated(value = EnumType.ORDINAL)
    private LearningLevel level;
    @Column(columnDefinition="LONGTEXT")
    private String blankedText;

    @OneToMany(mappedBy = "listeningQuiz")
    @Builder.Default
    private List<ListeningSubmit> submitList = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private List<String> answerList = new ArrayList<>();
}
