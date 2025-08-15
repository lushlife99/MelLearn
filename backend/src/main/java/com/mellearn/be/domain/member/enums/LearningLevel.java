package com.mellearn.be.domain.member.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum LearningLevel {

    Beginner(1),
    Intermediate(2),
    Advanced(3),

    ;

    private final int value;
}