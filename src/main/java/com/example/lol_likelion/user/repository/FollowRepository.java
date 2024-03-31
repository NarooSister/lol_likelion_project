package com.example.lol_likelion.user.repository;

import com.example.lol_likelion.user.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // Follow, Unfollow 유무
    @Query(value = "SELECT count(*) " +
            "FROM Follow " +
            "WHERE following_id = ?1 AND follower_id = ?2", nativeQuery = true)
    public int findByFollowingIdAndFollowerId(Long followerId, Long followingId);

    // unFollow
    @Modifying
    @Transactional
    public void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // 팔로워 리스트
    public List<Follow> findByFollowerId(Long followerId);

    // 팔로우 리스트
    public List<Follow> findByFollowingId(Long followingId);




}


