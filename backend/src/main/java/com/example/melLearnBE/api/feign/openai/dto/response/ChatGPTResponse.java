package com.example.melLearnBE.api.feign.openai.dto.response;

import com.example.melLearnBE.api.feign.openai.dto.request.Usage;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class ChatGPTResponse implements Serializable {
    private String id;
    private String object;
    private String model;
    private LocalDate created;
    private List<Choice> choices;
    private Usage usage;
}