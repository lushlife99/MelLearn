package com.mellearn.be.domain.quiz.choice.submit.repository.querydsl;

import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.listening.submit.dto.ListeningSubmitDto;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitDto;
import org.springframework.data.domain.Page;

public interface QuizSubmitRepositoryCustom {

    Page<QuizSubmitDto> findSubmitWithPaging(long memberId, QuizType quizType, int pageNumber, int pageSize);
    Page<ListeningSubmitDto> findListeningSubmitWithPaging(long memberId, int pageNumber, int pageSize);
    Page<SpeakingSubmitDto> findSpeakingSubmitWithPaging(long memberId, int pageNumber, int pageSize);
}
