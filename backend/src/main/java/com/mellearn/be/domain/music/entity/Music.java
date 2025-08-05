package com.mellearn.be.domain.music.entity;

import com.mellearn.be.domain.member.enums.Language;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Music {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String musicId;
    @Enumerated(value = EnumType.STRING)
    private Language language;
    private int liked;
    private int disLike;

    private boolean checkCategoryAvailable;
    private boolean speaking;
    private boolean grammar;
    private boolean listening;
    private boolean reading;
    private boolean vocabulary;

    public static Music create(String musicId, Language language, boolean checkCategoryAvailable) {
        return Music.builder()
                .musicId(musicId)
                .language(language)
                .checkCategoryAvailable(checkCategoryAvailable)
                .liked(0)
                .disLike(0)
                .speaking(false)
                .grammar(false)
                .listening(false)
                .reading(false)
                .vocabulary(false)
                .build();
    }
}
