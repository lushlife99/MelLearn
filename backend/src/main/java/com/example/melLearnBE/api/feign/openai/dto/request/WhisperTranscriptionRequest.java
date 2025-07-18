package com.example.melLearnBE.api.feign.openai.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WhisperTranscriptionRequest implements Serializable {

    private String model;
    private File file;
    private String response_format;
    private String language;
    private String prompt;
    private List<String> timestamp_granularities;
}