package com.example.melLearnBE.dto.response.openAI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrammarQuiz {

    private String question;
    private List<String> selectionList;
    private int answer;
    private String comment;

}
