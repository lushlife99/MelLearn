package com.mellearn.be.domain.quiz.listening.quiz.dto.response.chatmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ListeningQuizResponseDto(
        @JsonProperty(required = true, value = "blanked text") String blankedText,
        @JsonProperty(required = true, value = "answers") List<String> answers
        ) {
}
