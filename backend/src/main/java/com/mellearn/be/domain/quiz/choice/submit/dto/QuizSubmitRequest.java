package com.mellearn.be.domain.quiz.choice.submit.dto;

import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
import com.mellearn.be.domain.quiz.choice.submit.entity.QuizSubmit;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class QuizSubmitRequest {

    private String musicId;
    private QuizType quizType;
    private List<Integer> answers;

    public QuizSubmit toQuizSubmit(QuizList quizList, Member member) {
        return QuizSubmit.create(quizList, member, answers);
    }
}
