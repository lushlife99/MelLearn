package com.mellearn.be.api.feign.openai.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatQuestion {

    private int level;
    private String lang;
    private int totalProblem;
    private String text;
}
