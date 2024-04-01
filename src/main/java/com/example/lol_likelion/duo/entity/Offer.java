package com.example.lol_likelion.duo.entity;

import com.example.lol_likelion.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String status;

    @Setter
    private Long applyUserId;
    @Setter
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;
}
