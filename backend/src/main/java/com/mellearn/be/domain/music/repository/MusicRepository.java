package com.mellearn.be.domain.music.repository;


import com.mellearn.be.domain.music.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MusicRepository extends JpaRepository<Music, Long> {

    Optional<Music> findByMusicId(String musicId);
}
