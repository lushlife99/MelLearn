package com.example.melLearnBE.domain.listening.quiz.repository;

import com.example.melLearnBE.domain.member.enums.LearningLevel;
import com.example.melLearnBE.domain.listening.quiz.entity.ListeningQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ListeningQuizRepository extends JpaRepository<ListeningQuiz, Long> {
    Optional<ListeningQuiz> findByMusicIdAndLevel(String musicId, LearningLevel level);
}
