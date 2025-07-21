package com.mellearn.be.domain.quiz.comprehensive.dto;

import com.mellearn.be.domain.quiz.listening.submit.dto.ListeningSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitDto;
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
