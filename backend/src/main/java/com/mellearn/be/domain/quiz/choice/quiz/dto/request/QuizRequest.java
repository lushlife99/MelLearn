package com.mellearn.be.domain.quiz.choice.quiz.dto.request;

import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.entity.Quiz;
import com.mellearn.be.domain.quiz.choice.quiz.entity.QuizList;
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
public class QuizRequest {

    private String musicId;
    private QuizType quizType;
    private String lyric;

    public QuizList toQuizList(List<Quiz> quizzes, LearningLevel level) {
        return QuizList.create(quizType, quizzes, level, musicId);
    }
}
