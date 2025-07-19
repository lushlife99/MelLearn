package com.mellearn.be.domain.word.repository;


import com.mellearn.be.domain.word.entity.WordList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordListRepository extends JpaRepository<WordList, Long> {
}
