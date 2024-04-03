package com.example.lol_likelion.user.repository;

import com.example.lol_likelion.user.entity.BadgeState;
import com.example.lol_likelion.user.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);
    UserBadge findByUserIdAndBadgeId(Long userId, Long badgeId);
    Integer countAllByUserId(Long userId);
    List<UserBadge> findAllByUserId(Long userId);
    List<UserBadge> findAllByUserIdAndState(Long userId, BadgeState state);

}
