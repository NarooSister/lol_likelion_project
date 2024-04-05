package com.example.lol_likelion.auth.repository;

import com.example.lol_likelion.auth.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByGameNameAndTagLine(String gameName, String tagLine);
    boolean existsByUsername(String username);
    boolean existsByGameNameAndTagLine(String gameName, String tagLine);


}
