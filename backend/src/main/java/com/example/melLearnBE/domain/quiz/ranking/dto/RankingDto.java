package com.example.melLearnBE.domain.quiz.ranking.dto;

import com.example.melLearnBE.domain.quiz.ranking.entity.Ranking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingDto {

    private Long id;
    private String musicId;
    private Map<String, Double> score_list = new HashMap<>();

    public RankingDto(Ranking ranking) {
        this.id = ranking.getId();
        this.musicId = ranking.getMusicId();
        this.score_list = Map.copyOf(ranking.getScore_list());
    }
}