package com.mellearn.be.api.feign.openai.dto.response;

import lombok.Data;

@Data
public class ListeningAnswer {

    private String answerWord;
    private int lineIndex;
}
