package com.example.lol_likelion.auth.repository;

import com.example.lol_likelion.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByTagLineAndGameName(String tagLine, String gameName);

    // follow 에 사용 gameName/tagLine
    Optional<UserEntity> findByGameNameAndTagLine(String gameName, String tagLine);

}
