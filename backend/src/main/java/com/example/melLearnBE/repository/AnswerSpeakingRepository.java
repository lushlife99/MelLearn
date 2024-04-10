package com.example.melLearnBE.repository;

import com.example.melLearnBE.model.AnswerSpeaking;
import com.example.melLearnBE.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerSpeakingRepository extends JpaRepository<AnswerSpeaking, Long> {

    Optional<AnswerSpeaking> findByMusicIdAndMember(String musicId, Member member);
}
