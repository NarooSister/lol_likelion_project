package com.example.lol_likelion.duo.repository;

import com.example.lol_likelion.duo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsByStatusAndUserId(String status, Long userId);

    List<Post> findAllByOrderByCreatedAtDesc();
}
