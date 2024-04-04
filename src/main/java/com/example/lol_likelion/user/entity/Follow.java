package com.example.lol_likelion.user.entity;

import com.example.lol_likelion.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    // fromUser 나를
    private UserEntity follower;


    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    // toUser 내가
    private UserEntity following;

    @Setter
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Setter
    private Long followingUser;
    @Setter
    private Long followerUser;

    public Follow(UserEntity follower, UserEntity following, LocalDateTime createdAt) {
        this.follower = follower;
        this.following = following;
        this.createdAt = createdAt;
    }
}