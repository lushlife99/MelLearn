package com.mellearn.be.domain.quiz.choice.submit.repository.querydsl;

import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.choice.submit.entity.QuizSubmit;
import com.mellearn.be.domain.quiz.listening.submit.dto.ListeningSubmitDto;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface QuizSubmitRepositoryCustom {

    List<QuizSubmitDto> findSubmitWithPaging(long memberId, QuizType quizType, Long lastSeenId, int pageSize);
    List<ListeningSubmitDto> findListeningSubmitWithPaging(long memberId, Long lastSeenId, int pageSize);
    List<SpeakingSubmitDto> findSpeakingSubmitWithPaging(long memberId, Long lastSeenId, int pageSize);
}
