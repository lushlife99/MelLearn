package com.mellearn.be.api.feign.openai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class BatchResponse {

    private String id;
    private String object;

    private String status; // "validating", "in_progress", "completed", ...

    @JsonProperty("input_file_id")
    private String inputFileId;

    private String endpoint;

    @JsonProperty("completion_window")
    private String completionWindow;

    private Map<String, Object> metadata;

    @JsonProperty("output_file_id")
    private String outputFileId;

    @JsonProperty("error_file_id")
    private String errorFileId;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("in_progress_at")
    private String inProgressAt;

    @JsonProperty("completed_at")
    private String completedAt;

    @JsonProperty("expires_at")
    private String expiresAt;
}
