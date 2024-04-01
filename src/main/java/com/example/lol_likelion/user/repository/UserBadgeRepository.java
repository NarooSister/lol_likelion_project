package com.example.lol_likelion.user.repository;

import com.example.lol_likelion.user.entity.BadgeState;
import com.example.lol_likelion.user.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);

}
