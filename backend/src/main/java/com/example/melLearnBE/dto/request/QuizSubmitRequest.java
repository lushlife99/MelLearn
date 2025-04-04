package com.example.melLearnBE.dto.request;

import com.example.melLearnBE.enums.QuizType;
import com.example.melLearnBE.model.Member;
import com.example.melLearnBE.model.QuizList;
import com.example.melLearnBE.model.QuizSubmit;
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
