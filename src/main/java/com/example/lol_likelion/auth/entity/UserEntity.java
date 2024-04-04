package com.example.lol_likelion.auth.entity;

import com.example.lol_likelion.duo.entity.Offer;
import com.example.lol_likelion.duo.entity.Post;
import com.example.lol_likelion.user.entity.Badge;
import com.example.lol_likelion.user.entity.Follow;
import com.example.lol_likelion.user.entity.Quest;
import com.example.lol_likelion.user.entity.UserBadge;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @Setter
    private String puuid;
    @Setter
    private String password;
    @Setter
    private String gameName;
    @Setter
    private String tagLine;

    @Setter
    private String email;

    @Setter
    private String phone;
    @Setter
    private String tier;
    @Setter
    private Integer dailyGameCount; //하루에 플레이 한 게임 카운트-> 잔디 구현을 위해서는 List나 Set으로 고쳐야 함

    @Setter
    private Integer profileIconId;

    @Setter
    private Integer leagueWins;
    @Setter
    private Integer leagueLosses;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Setter
    private LocalDateTime updatedAt;        // 유저 페이지에서 최근 업데이트 된 시간

    @Setter
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private List<Follow> followerList;

    @Setter
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private List<Follow> followingList;

    @Builder.Default
    private String roles = "ROLE_USER";

    @Setter
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Quest quest;

    @Setter
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserBadge> userBadges;

    @Setter
    private Integer trustScore;
    @Setter
    private Integer level;

    @Setter
    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    private List<Post> post;

    @Setter
    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    private List<Offer> offer;

    //유저 생성할 때 Quest도 생성
    @PrePersist
    public void initializeQuest() {
        if (this.quest == null) {
            Quest newQuest = new Quest();
            // Quest 기본값 설정
            newQuest.setUser(this); // 양방향 설정
            this.quest = newQuest;
        }
    }

}
