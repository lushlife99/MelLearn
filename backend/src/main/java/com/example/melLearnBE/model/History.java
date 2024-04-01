package com.example.melLearnBE.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class History {
    @Id @GeneratedValue
    private Long id;
    private double score;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    @OneToOne
    private ProblemList problemList;
    @CreationTimestamp
    private LocalDateTime createdTime;

}
