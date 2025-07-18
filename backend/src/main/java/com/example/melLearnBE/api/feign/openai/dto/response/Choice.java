package com.example.melLearnBE.api.feign.openai.dto.response;

import com.example.melLearnBE.api.feign.openai.dto.request.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Choice implements Serializable {
    private Integer index;
    private Message message;
    @JsonProperty("finish_reason")
    private String finishReason;
}
