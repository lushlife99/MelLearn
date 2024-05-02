package com.example.melLearnBE.repository;

import com.example.melLearnBE.model.SpeakingSubmit;
import com.example.melLearnBE.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpeakingSubmitRepository extends JpaRepository<SpeakingSubmit, Long> {

    Optional<SpeakingSubmit> findByMusicIdAndMember(String musicId, Member member);
}
