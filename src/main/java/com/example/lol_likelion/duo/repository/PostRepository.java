package com.example.lol_likelion.duo.repository;

import com.example.lol_likelion.duo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
