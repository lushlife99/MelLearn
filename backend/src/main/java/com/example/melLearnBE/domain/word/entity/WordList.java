package com.example.melLearnBE.domain.word.entity;

import com.example.melLearnBE.domain.member.entity.Member;
import com.example.melLearnBE.domain.member.enums.Language;
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
public class WordList {
    @Id @GeneratedValue
    private Long id;
    @OneToMany(mappedBy = "wordList")
    @Builder.Default
    private List<Word> words = new ArrayList<>();
    @ManyToOne
    private Member member;
    @Enumerated(EnumType.ORDINAL)
    private Language langType;

}