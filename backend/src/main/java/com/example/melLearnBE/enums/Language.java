package com.example.melLearnBE.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Language {
    ENGLISH("en"),
    JAPANESE("ja")

    ;

    private final String iso639;
}
