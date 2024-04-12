package com.example.melLearnBE.dto.request;

import com.example.melLearnBE.enums.QuizType;
import lombok.Data;

import java.util.List;

@Data
public class QuizSubmitRequest {

    private String musicId;
    private QuizType quizType;
    private List<Integer> answers;
}
