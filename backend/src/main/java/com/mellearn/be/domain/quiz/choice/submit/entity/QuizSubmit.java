package com.mellearn.be.domain.quiz.choice.submit.entity;

import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.quiz.choice.quiz.entity.Quiz;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizSubmit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private QuizType quizType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_list_id")
    @BatchSize(size = 5)
    private QuizList quizList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ElementCollection
    @CollectionTable(name = "quiz_submit_answer", joinColumns = @JoinColumn(name = "quiz_submit_id"))
    @Column(name = "answer")
    @OrderColumn(name = "answer_order")
    @BatchSize(size = 5)
    private List<Integer> submitAnswerList;

    private int score;

    @CreationTimestamp
    private LocalDateTime createdTime;

    @Builder
    public QuizSubmit(QuizList quizList, Member member, List<Integer> submitAnswerList, QuizType quizType, int score) {
        this.quizList = quizList;
        this.member = member;
        this.quizType = quizList.getQuizType();
        this.submitAnswerList = submitAnswerList;
        this.score = score;
        this.createdTime = LocalDateTime.now();
    }

    public static QuizSubmit create(QuizList quizList, Member member, List<Integer> submitAnswerList) {
        return QuizSubmit.builder()
                .quizList(quizList)
                .member(member)
                .quizType(quizList.getQuizType())
                .submitAnswerList(submitAnswerList)
                .score(calculateScore(submitAnswerList, quizList.getQuizzes()))
                .build();
    }

    private static int calculateScore(List<Integer> submitAnswers, List<Quiz> quizzes) {
        int correctCount = 0;
        for (int i = 0; i < submitAnswers.size(); i++) {
            if (submitAnswers.get(i).equals(quizzes.get(i).getAnswer())) {
                correctCount++;
            }
        }
        return (int) ((double) correctCount / quizzes.size() * 100);
    }
}
