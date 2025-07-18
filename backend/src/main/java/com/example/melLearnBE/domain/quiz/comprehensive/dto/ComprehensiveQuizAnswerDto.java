package com.example.melLearnBE.domain.quiz.comprehensive.dto;

import com.example.melLearnBE.domain.listening.submit.dto.ListeningSubmitDto;
import com.example.melLearnBE.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.example.melLearnBE.domain.quiz.speaking.dto.SpeakingSubmitDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprehensiveQuizAnswerDto {

    private ListeningSubmitDto listeningSubmit;
    private SpeakingSubmitDto speakingSubmit;
    private QuizSubmitDto vocabularySubmit;
    private QuizSubmitDto readingSubmit;
    private QuizSubmitDto grammarSubmit;
}
