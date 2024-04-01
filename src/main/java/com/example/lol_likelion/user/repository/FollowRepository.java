package com.example.lol_likelion.user.repository;

import com.example.lol_likelion.user.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // follow query
    @Modifying
    @Query(value =  "INSERT INTO follow(following_id, follower_id, created_at) " +
                    "VALUES(:following_id, :follower_id, now())", nativeQuery = true)
    void follow(@Param("following_id") Long followingId, @Param("follower_id") Long followerId);

    // unfollow query
    @Modifying
    @Query(value =  "DELETE FROM follow " +
                    "WHERE following_id = :following_id AND follower_id = :follower_id", nativeQuery = true)
    void unfollow(@Param("following_id") Long followingId, @Param("follower_id") Long followerId);

    // follow 여부 query
    @Query(value =  "SELECT COUNT(*) FROM follow " +
                    "WHERE following_id = :principalId AND follower_id = :pageUserId", nativeQuery = true)
    Long followState(@Param("principalId") Long principalId, @Param("pageUserId") Long pageUserId);

    // follower 수 query
    @Query(value =  "SELECT COUNT(*) FROM follow " +
                    "WHERE follower_id = :pageUserId", nativeQuery = true)
    Long followerCount(@Param("pageUserId") Long pageUserId);

    // following 수 query
    @Query(value =  "SELECT COUNT(*) FROM follow " +
                    "WHERE following_id = :pageUserId", nativeQuery = true)
    Long followingCount(@Param("pageUserId") Long pageUserId);

}


