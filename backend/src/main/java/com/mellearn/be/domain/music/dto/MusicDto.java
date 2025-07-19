package com.mellearn.be.domain.music.dto;

import com.mellearn.be.domain.member.enums.Language;
import com.mellearn.be.domain.music.entity.Music;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MusicDto {

    private Long id;
    private String musicId;
    private Language language;
    private int liked;
    private int disLike;
    private boolean speaking;
    private boolean grammar;
    private boolean listening;
    private boolean reading;
    private boolean vocabulary;

    public MusicDto(Music music) {
        this.id = music.getId();
        this.musicId = music.getMusicId();
        this.language = music.getLanguage();
        this.liked = music.getLiked();
        this.disLike = music.getDisLike();
        this.speaking = music.isSpeaking();
        this.grammar = music.isGrammar();
        this.listening = music.isListening();
        this.reading = music.isReading();
        this.vocabulary = music.isVocabulary();
    }
}
