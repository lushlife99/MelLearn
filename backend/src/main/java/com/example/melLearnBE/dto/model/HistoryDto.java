package com.example.melLearnBE.dto.model;

import com.example.melLearnBE.model.History;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDto {

    private Long id;
    private double score;
    private MemberDto user;
    private ProblemListDto problemList;
    private LocalDateTime createdTime;

    public HistoryDto(History history) {
        this.id = history.getId();
        this.score = history.getScore();
        this.user = new MemberDto(history.getMember());
        this.problemList = new ProblemListDto(history.getProblemList());
        this.createdTime = history.getCreatedTime();
    }
}
