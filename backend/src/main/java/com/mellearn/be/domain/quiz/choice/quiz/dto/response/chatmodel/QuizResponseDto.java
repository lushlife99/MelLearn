package com.mellearn.be.domain.quiz.choice.quiz.dto.response.chatmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QuizResponseDto(
        @JsonProperty(required = true, value = "question of quiz") String question,
        @JsonProperty(required = true, value = "option list of quiz. option list's size is must be 4") List<String> optionList,
        @JsonProperty(required = true, value = "answer of quiz. answer must start from 1") int answer,
        @JsonProperty(required = true, value = "quiz commentary") String comment
) {
}
