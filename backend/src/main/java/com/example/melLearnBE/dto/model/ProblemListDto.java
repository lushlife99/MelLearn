package com.example.melLearnBE.dto.model;

import com.example.melLearnBE.model.Problem;
import com.example.melLearnBE.model.ProblemList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemListDto {

    private Long id;
    private List<Problem> problems;
    private Long musicId;
    private int level;
    private LocalDateTime createdTime;

    public ProblemListDto(ProblemList problemList) {
        this.id = problemList.getId();
        this.problems = problemList.getProblems();
        this.musicId = problemList.getMusicId();
        this.level = problemList.getLevel();
        this.createdTime = problemList.getCreatedTime();
    }
}
