package com.example.melLearnBE.repository;

import com.example.melLearnBE.model.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
