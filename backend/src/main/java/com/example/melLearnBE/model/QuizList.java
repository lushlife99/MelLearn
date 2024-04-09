package com.example.melLearnBE.model;

import com.example.melLearnBE.enums.QuizType;
import jakarta.persistence.*;
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
public class QuizList {
    @Id @GeneratedValue
    private Long id;
    @Enumerated(value = EnumType.ORDINAL)
    private QuizType quizType;
    @OneToMany(mappedBy = "quizList", cascade = CascadeType.ALL)
    private List<Quiz> quizzes;
    @OneToMany(mappedBy = "quizList", cascade = CascadeType.ALL)
    private List<QuizSubmit> submitList;
    private String musicId;
    private int level;
    @CreationTimestamp
    private LocalDateTime createdTime;
}
