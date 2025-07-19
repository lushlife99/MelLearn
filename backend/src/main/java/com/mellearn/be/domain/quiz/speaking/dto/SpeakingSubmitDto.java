package com.mellearn.be.domain.quiz.speaking.dto;

import com.mellearn.be.domain.quiz.speaking.entity.SpeakingSubmit;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
public class SpeakingSubmitDto {

    private Long id;
    private String musicId;
    private String submit;
    private String markedText;
    private double score;
    private LocalDateTime createdTime;

    public SpeakingSubmitDto(SpeakingSubmit speakingSubmit) {
        this.id = speakingSubmit.getId();
        this.musicId = speakingSubmit.getMusicId();
        this.submit = speakingSubmit.getSubmit();
        this.markedText = speakingSubmit.getMarkedText();
        this.score = speakingSubmit.getScore();
        this.createdTime = speakingSubmit.getCreatedTime();
    }

    @QueryProjection
    public SpeakingSubmitDto(Long id, String musicId, String submit, String markedText, double score, LocalDateTime createdTime) {
        this.id = id;
        this.musicId = musicId;
        this.submit = submit;
        this.markedText = markedText;
        this.score = score;
        this.createdTime = createdTime;
    }
}
