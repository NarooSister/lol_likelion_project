package com.example.lol_likelion.user.entity;

import com.example.lol_likelion.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity fromUser;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity toUser;

    @Setter
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Follow(UserEntity fromUser, UserEntity toUser, LocalDateTime createdAt) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.createdAt = createdAt;
    }
}