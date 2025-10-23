package com.mellearn.be.domain.quiz.choice.submit.repository.querydsl;

import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.choice.quiz.entity.enums.QuizType;
import com.mellearn.be.domain.quiz.choice.submit.dto.MusicQuizSubmit;
import com.mellearn.be.domain.quiz.choice.submit.dto.QuizSubmitDto;
import com.mellearn.be.domain.quiz.listening.submit.dto.ListeningSubmitDto;
import com.mellearn.be.domain.quiz.speaking.dto.SpeakingSubmitDto;

import java.time.LocalDateTime;
import java.util.List;

public interface QuizSubmitRepositoryCustom {

    List<QuizSubmitDto> findHistoryPage(long memberId, QuizType quizType, Long lastSeenId, int pageSize);
    List<ListeningSubmitDto> findListeningHistoryPage(long memberId, Long lastSeenId, int pageSize);
    List<SpeakingSubmitDto> findSpeakingSubmitWithPaging(long memberId, Long lastSeenId, int pageSize);
    List<MusicQuizSubmit> findSubmitPage(LearningLevel level, LocalDateTime lastSeen);

}
