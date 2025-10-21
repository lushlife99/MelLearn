package com.mellearn.be.domain.quiz.listening.quiz.entity;

import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.dto.request.QuizRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "listening_quiz", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"music_id", "level"})
})
public class ListeningQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String blankedText;

    private String musicId;

    @Enumerated(EnumType.STRING)
    private LearningLevel level;

    @ElementCollection
    @CollectionTable(name = "listening_quiz_answer", joinColumns = @JoinColumn(name = "listening_quiz_id"))
    @Column(name = "answer")
    private List<String> answerList;

    private LocalDateTime createdTime;

    @Builder
    public ListeningQuiz(String blankedText, String musicId, LearningLevel level, List<String> answerList, Long id) {
        this.blankedText = blankedText;
        this.musicId = musicId;
        this.level = level;
        this.answerList = answerList;
        this.createdTime = LocalDateTime.now();
        this.id = id;
    }

    public static ListeningQuiz create(String blankedText, QuizRequest quizRequest, List<String> answerList) {
        return ListeningQuiz.builder()
                .blankedText(blankedText)
                .musicId(quizRequest.getMusicId())
                .level(quizRequest.getLearningLevel())
                .answerList(answerList)
                .build();
    }
}
