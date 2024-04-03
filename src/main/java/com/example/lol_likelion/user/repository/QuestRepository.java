package com.example.lol_likelion.user.repository;

import com.example.lol_likelion.user.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestRepository extends JpaRepository<Quest, Long> {
}
