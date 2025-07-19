package com.mellearn.be.domain.listening.quiz.repository;

import com.mellearn.be.domain.listening.quiz.entity.ListeningQuiz;
import com.mellearn.be.domain.member.enums.LearningLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ListeningQuizRepository extends JpaRepository<ListeningQuiz, Long> {
    Optional<ListeningQuiz> findByMusicIdAndLevel(String musicId, LearningLevel level);
}
