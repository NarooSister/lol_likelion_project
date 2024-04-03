package com.example.lol_likelion.user.repository;

import com.example.lol_likelion.user.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowJPARepository extends JpaRepository<Follow, Long > {
    boolean existsByFollowerIdAndFollowingId(Long followerId , Long followingId);
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId , Long followingId);


}
