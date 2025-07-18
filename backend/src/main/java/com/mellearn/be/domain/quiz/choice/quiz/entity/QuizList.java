package com.mellearn.be.domain.quiz.choice.quiz.entity;

import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.submit.entity.QuizSubmit;
import com.mellearn.be.global.prompt.QuizType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "quiz_list", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"music_id", "level", "quiz_type"})
})
public class QuizList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private QuizType quizType;
    @OneToMany(mappedBy = "quizList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizzes = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private LearningLevel level;
    @OneToMany(mappedBy = "quizList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuizSubmit> submitList = new ArrayList<>();
    private String musicId;
    @CreationTimestamp
    private LocalDateTime createdTime;

    @Builder
    public QuizList(QuizType quizType, List<Quiz> quizzes, LearningLevel level, String musicId, Long id) {
        this.quizType = quizType;
        this.quizzes = quizzes;
        this.level = level;
        this.musicId = musicId;
        this.createdTime = LocalDateTime.now();
        this.id = id;
    }

    public static QuizList create(QuizType quizType, List<Quiz> quizzes, LearningLevel level, String musicId) {
        return QuizList.builder()
                .quizType(quizType)
                .quizzes(quizzes)
                .level(level)
                .musicId(musicId)
                .build();
    }

    public void addQuiz(Quiz quiz) {
        quizzes.add(quiz);
        quiz.setQuizList(this);
    }

    public void addQuizzes(List<Quiz> newQuizzes) {
        newQuizzes.forEach(this::addQuiz);
    }
}
