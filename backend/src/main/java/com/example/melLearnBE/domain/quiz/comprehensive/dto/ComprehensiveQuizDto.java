package com.example.melLearnBE.domain.quiz.comprehensive.dto;

import com.example.melLearnBE.domain.listening.quiz.dto.ListeningQuizDto;
import com.example.melLearnBE.domain.quiz.choice.quiz.dto.QuizListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprehensiveQuizDto {

    private QuizListDto grammarQuiz;
    private QuizListDto vocaQuiz;
    private QuizListDto readingQuiz;
    private ListeningQuizDto listeningQuizDto;
}
