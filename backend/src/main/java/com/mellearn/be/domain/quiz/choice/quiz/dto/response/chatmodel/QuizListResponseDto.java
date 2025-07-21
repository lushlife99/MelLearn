package com.mellearn.be.domain.quiz.choice.quiz.dto.response.chatmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QuizListResponseDto(
        @JsonProperty(required = true, value = "list of quiz") List<QuizResponseDto> quizzes
) {
}
