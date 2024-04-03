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
    private Integer continuousDailyGame=0;

    // 연속적인 승리 횟수
    // 10연승
    private Integer winningStreak=0;

    // 승리 횟수
    // 100승
    private Integer winning=0;

    // 트리플킬 횟수
    // 10회
    private Integer triplekillCount=0;

    // 쿼드라킬 횟수
    // 5회
    private Integer quadrakillCount=0;

    // 펜타킬 횟수
    // 3회
    private Integer pentakillCount=0;

    // 제어와수 설치 횟수
    // 50회
    private Integer visionWardPlaced=0;

    // 와드 파괴 횟수
    // 50회
    private Integer wardsTakedowns=0;

    // 퍼스트 블러드 횟수
    // 10회
    private Integer firstBloodCount=0;

}
