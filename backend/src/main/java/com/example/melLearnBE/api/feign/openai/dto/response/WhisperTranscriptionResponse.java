package com.example.melLearnBE.api.feign.openai.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WhisperTranscriptionResponse implements Serializable {
    private String text;
    private List<WhisperSegment> segments;
}