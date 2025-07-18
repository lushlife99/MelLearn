package com.example.melLearnBE.domain.quiz.ranking.repository;

import com.example.melLearnBE.domain.quiz.ranking.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking, Long> {

    Optional<Ranking> findByMusicId(String musicId);
}
