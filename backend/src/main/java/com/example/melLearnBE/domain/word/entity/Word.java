package com.example.melLearnBE.domain.word.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Word {
    @Id @GeneratedValue
    private Long id;
    @ManyToOne
    private WordList wordList;
    private String content;

}
