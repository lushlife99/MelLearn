package com.example.melLearnBE.dto.model;

import com.example.melLearnBE.model.SpeakingSubmit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpeakingSubmitDto {

    private Long id;
    private String musicId;
    private Long memberId;
    private String submit;
    private String markedText;
    private double score;

    public SpeakingSubmitDto(SpeakingSubmit speakingSubmit) {
        this.id = speakingSubmit.getId();
        this.musicId = speakingSubmit.getMusicId();
        this.memberId = speakingSubmit.getMember().getId();
        this.submit = speakingSubmit.getSubmit();
        this.markedText = speakingSubmit.getMarkedText();
        this.score = speakingSubmit.getScore();
    }
}
