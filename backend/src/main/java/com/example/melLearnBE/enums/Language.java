package com.example.melLearnBE.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Language {
    ENGLISH("en"),
    JAPANESE("ja")

    ;

    private final String iso639Value;

    public static Language valueOfIso(String iso639Value) {
        for (Language language : Language.values()) {
            if (language.iso639Value.equals(iso639Value)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Invalid phaseNum: " + iso639Value);
    }
}
