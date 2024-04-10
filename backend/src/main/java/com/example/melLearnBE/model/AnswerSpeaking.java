package com.example.melLearnBE.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Column(columnDefinition="LONGTEXT")
    private String submit;
    @Column(columnDefinition="LONGTEXT")
    private String markedText;
    private double score;

}
