package com.mellearn.be.domain.music.chart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChartDto {

    private String title;
    private List<TrackDto> tracks;

    @Getter
    @AllArgsConstructor
    public static class TrackDto {
        String id;
        String name;
    }
}
