package com.mellearn.be.api.feign.openai.dto.response;

import com.mellearn.be.domain.quiz.choice.quiz.entity.Quiz;
import lombok.Data;

import java.util.List;

@Data
public class Content {
    private List<Quiz> probList;
}
