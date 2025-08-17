package com.mellearn.be.api.feign.openai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchRequest {

    @JsonProperty("input_file_id")
    private String inputFileId;

    private String endpoint;

    @JsonProperty("completion_window")
    private String completionWindow;

    private Map<String, Object> metadata;
}

