package com.example.melLearnBE.dto.request;

import com.example.melLearnBE.enums.LearningLevel;
import com.example.melLearnBE.enums.QuizType;
import com.example.melLearnBE.model.Quiz;
import com.example.melLearnBE.model.QuizList;
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
