package com.example.melLearnBE.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LrcLyric {

    private long startMs;
    private long durMs;
    private String text;
}
