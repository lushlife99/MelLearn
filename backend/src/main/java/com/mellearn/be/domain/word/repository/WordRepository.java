package com.mellearn.be.domain.word.repository;


import com.mellearn.be.domain.word.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, JpaRepository> {
}
