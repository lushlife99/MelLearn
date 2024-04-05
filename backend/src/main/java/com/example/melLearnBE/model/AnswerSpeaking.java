package com.example.melLearnBE.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class AnswerSpeaking {

    @Id @GeneratedValue
    private Long id;
    private String musicId;

    @ManyToOne
    private Member member;

    @Lob
    private String markedLyric;
    private double score;
}
