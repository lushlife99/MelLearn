package com.mellearn.be.domain.word.entity;

import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.member.enums.Language;
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
    @Enumerated(EnumType.STRING)
    private Language langType;

}