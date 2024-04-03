package com.example.lol_likelion.user.entity;

import com.example.lol_likelion.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;


@Getter
@Entity
@Setter
@NoArgsConstructor
public class Quest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 연속적인 게임 일수
    // 조건 - 7일, 30일)
    @Column(columnDefinition = "integer default 0")
    private Integer continuousDailyGame;

    // 연속적인 승리 횟수
    // 10연승
    @Column(columnDefinition = "integer default 0")
    private Integer winningStreak;

    // 승리 횟수
    // 100승
    @Column(columnDefinition = "integer default 0")
    private Integer winning;

    // 트리플킬 횟수
    // 10회
    @Column(columnDefinition = "integer default 0")
    private Integer triplekillCount;

    // 쿼드라킬 횟수
    // 5회
    @Column(columnDefinition = "integer default 0")
    private Integer quadrakillCount;

    // 펜타킬 횟수
    // 3회
    @Column(columnDefinition = "integer default 0")
    private Integer pentakillCount;

    // 제어와수 설치 횟수
    // 50회
    @Column(columnDefinition = "integer default 0")
    private Integer visionWardPlaced;

    // 와드 파괴 횟수
    // 50회
    @Column(columnDefinition = "integer default 0")
    private Integer wardsTakedowns;

    // 퍼스트 블러드 횟수
    // 10회
    @Column(columnDefinition = "integer default 0")
    private Integer firstBloodCount;

}
