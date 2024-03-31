package com.example.lol_likelion.user.entity;

import com.example.lol_likelion.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserBadge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id")
    private Badge badge;

    @Enumerated(EnumType.STRING)
    private BadgeState state;   //미획득, 획득, 대표 뱃지

    @CreationTimestamp
    private LocalDateTime createdAt;
}
