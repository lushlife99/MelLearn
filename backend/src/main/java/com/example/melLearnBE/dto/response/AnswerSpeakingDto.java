package com.example.melLearnBE.dto.response;

import com.example.melLearnBE.model.AnswerSpeaking;
import com.example.melLearnBE.model.Member;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerSpeakingDto {

    private Long id;
    private String musicId;
    private Long memberId;
    private String submit;
    private String markedText;
    private double score;

    public AnswerSpeakingDto(AnswerSpeaking answerSpeaking) {
        this.id = answerSpeaking.getId();
        this.musicId = answerSpeaking.getMusicId();
        this.memberId = answerSpeaking.getMember().getId();
        this.submit = answerSpeaking.getSubmit();
        this.markedText = answerSpeaking.getMarkedText();
        this.score = answerSpeaking.getScore();
    }
}
