package com.example.lol_likelion.user.repository;

import com.example.lol_likelion.user.entity.BadgeState;
import com.example.lol_likelion.user.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);
    UserBadge findByUserIdAndBadgeId(Long userId, Long badgeId);
    Integer countAllByUserId(Long userId);
    List<UserBadge> findAllByUserId(Long userId);
    List<UserBadge> findAllByUserIdAndState(Long userId, BadgeState state);
    Optional<UserBadge> findByUserIdAndBadgeIdAndState(Long userId, Long badgeId, BadgeState state);
    @Query("SELECT ub FROM UserBadge ub WHERE ub.user.id = :userId AND (ub.state = :state1 OR ub.state = :state2)")
    List<UserBadge> findAllByUserIdAndStates(@Param("userId") Long userId, @Param("state1") BadgeState state1, @Param("state2") BadgeState state2);

}
