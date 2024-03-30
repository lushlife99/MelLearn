package com.example.melLearnBE.model;

import com.example.melLearnBE.enums.ProbType;
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
@Builder
@Entity
public class Problem {
    @Id @GeneratedValue
    private Long id;
    private Long musicId;
    @Enumerated(value = EnumType.ORDINAL)
    private ProbType probType;
    private String question;
    @ElementCollection
    @Builder.Default
    private List<String> selection = new ArrayList<>(4);
    private int answer;
    private String comment;

    @ManyToOne
    private ProblemList problemList;


}