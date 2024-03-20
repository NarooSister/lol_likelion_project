package com.example.lol_likelion.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Zandi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Integer userId;
    @Setter
    private Integer gameCount;
    @Setter
    private LocalDateTime createAt;

    public Zandi(Integer userId, Integer gameCount, LocalDateTime createAt) {
        this.userId = userId;
        this.gameCount = gameCount;
        this.createAt = createAt;
    }

}