package com.example.melLearnBE.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class ProblemList {
    @Id @GeneratedValue
    private Long id;
    @OneToMany(mappedBy = "problemList")
    private List<Problem> problems;
    private Long musicId;
    private int level;
    @CreationTimestamp
    private LocalDateTime createdTime;
}
