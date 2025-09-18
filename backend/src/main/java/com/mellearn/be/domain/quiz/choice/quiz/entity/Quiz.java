package com.mellearn.be.domain.quiz.choice.quiz.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition="LONGTEXT")
    private String question;

    @ElementCollection
    @CollectionTable(name = "quiz_option", joinColumns = @JoinColumn(name = "quiz_id"))
    @Column(name = "option_text")
    @BatchSize(size = 50)
    private List<String> optionList = new ArrayList<>(4);

    private int answer;

    @Column(columnDefinition="LONGTEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_list_id")
    private QuizList quizList;

    @Builder
    public Quiz(String question, List<String> optionList, int answer, String comment, Long id) {
        this.question = question;
        this.optionList = optionList;
        this.answer = answer;
        this.comment = comment;
        this.id = id;
    }

    public static Quiz create(String question, List<String> optionList, int answer, String comment) {
        return Quiz.builder()
                .question(question)
                .optionList(optionList)
                .answer(answer)
                .comment(comment)
                .build();
    }

    public void setQuizList(QuizList quizList) {
        this.quizList = quizList;
    }
}