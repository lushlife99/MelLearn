package com.example.melLearnBE.dto.request;

import com.example.melLearnBE.enums.QuizType;
import lombok.Data;

@Data
public class QuizRequest {

    private String musicId;
    private QuizType quizType;
    private String lyric;
}
