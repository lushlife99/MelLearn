package com.example.melLearnBE.api.feign.openai.dto.response;

import com.example.melLearnBE.domain.quiz.choice.quiz.entity.Quiz;
import lombok.Data;

import java.util.List;

@Data
public class Content {
    private List<Quiz> probList;
}
