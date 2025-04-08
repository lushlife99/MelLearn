package com.example.melLearnBE.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private String word;

    @ElementCollection
    @CollectionTable(name = "quiz_option", joinColumns = @JoinColumn(name = "quiz_id"))
    @Column(name = "option_text")
    private List<String> optionList = new ArrayList<>(4);

    private int answer;

    @Column(columnDefinition="LONGTEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_list_id")
    private QuizList quizList;

    private int submitCount;

    private int correctCount;

    @Builder
    public Quiz(String question, String word, List<String> optionList, int answer, String comment) {
        this.question = question;
        this.word = word;
        this.optionList = optionList;
        this.answer = answer;
        this.comment = comment;
        this.submitCount = 0;
        this.correctCount = 0;
    }

    public static Quiz create(String question, String word, List<String> optionList, int answer, String comment) {
        return Quiz.builder()
                .question(question)
                .word(word)
                .optionList(optionList)
                .answer(answer)
                .comment(comment)
                .build();
    }

    public void setQuizList(QuizList quizList) {
        this.quizList = quizList;
    }

    public void incrementSubmitCount() {
        this.submitCount++;
    }

    public void incrementCorrectCount() {
        this.correctCount++;
    }
}