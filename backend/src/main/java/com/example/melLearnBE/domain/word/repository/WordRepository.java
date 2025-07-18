package com.example.melLearnBE.domain.word.repository;

import com.example.melLearnBE.domain.word.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, JpaRepository> {
}
