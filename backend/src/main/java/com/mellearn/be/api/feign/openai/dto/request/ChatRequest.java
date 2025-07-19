package com.mellearn.be.api.feign.openai.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest implements Serializable {
    private String system;
    private String userInput;
}