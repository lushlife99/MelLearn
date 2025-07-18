package com.example.melLearnBE.domain.music.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LrcLyric implements Serializable {

    private long startMs;
    private long durMs;
    private String text;
}
