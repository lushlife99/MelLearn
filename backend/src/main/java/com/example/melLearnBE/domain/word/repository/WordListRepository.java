package com.example.melLearnBE.domain.word.repository;

import com.example.melLearnBE.domain.word.entity.WordList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordListRepository extends JpaRepository<WordList, Long> {
}
