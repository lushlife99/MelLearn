package com.mellearn.be.domain.quiz.choice.quiz.entity.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum QuizType {
    GRAMMAR,
    VOCABULARY,
    LISTENING,
    READING,
    SPEAKING
}
