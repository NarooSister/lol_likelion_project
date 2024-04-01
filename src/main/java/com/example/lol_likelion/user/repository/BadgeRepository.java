package com.example.lol_likelion.user.repository;

import com.example.lol_likelion.user.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

}
