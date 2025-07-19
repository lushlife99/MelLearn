package com.mellearn.be.domain.quiz.speaking.repository;

import com.mellearn.be.domain.member.entity.Member;
import com.mellearn.be.domain.quiz.speaking.entity.SpeakingSubmit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SpeakingSubmitRepository extends JpaRepository<SpeakingSubmit, Long> {

    @Query("SELECT ss FROM SpeakingSubmit ss WHERE ss.musicId = :musicId AND ss.member = :member ORDER BY ss.score DESC")
    Optional<SpeakingSubmit> findTopByMusicIdAndMemberOrderByScoreDesc(String musicId, Member member);
}
