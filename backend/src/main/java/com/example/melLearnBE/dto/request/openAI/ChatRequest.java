package com.example.melLearnBE.dto.request.openAI;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatRequest implements Serializable {
    private String system;
    private String question;
}