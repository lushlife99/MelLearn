package com.mellearn.be.domain.quiz.listening.quiz.repository;

import com.mellearn.be.domain.member.enums.LearningLevel;
import com.mellearn.be.domain.quiz.listening.quiz.entity.ListeningQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListeningQuizRepository extends JpaRepository<ListeningQuiz, Long> {
    Optional<ListeningQuiz> findByMusicIdAndLevel(String musicId, LearningLevel level);
    List<ListeningQuiz> findByMusicIdIn(List<String> musicIds);
}
