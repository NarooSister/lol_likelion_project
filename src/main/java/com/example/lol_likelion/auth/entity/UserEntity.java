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


import java.time.LocalDateTime;
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
    private Integer dailyGameCount;
    @Setter
    private Integer profileIconId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Setter
    private LocalDateTime updatedAt;        // 유저 페이지에서 최근 업데이트 된 시간

    @Setter
    @OneToMany(mappedBy = "following")
    private List<Follow> followerList;

    @Setter
    @OneToMany(mappedBy = "follower")
    private List<Follow> followingList;

    @Builder.Default
    private String roles = "ROLE_USER";

    @Setter
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Quest quest;

    @Setter
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserBadge> userBadges = new HashSet<>();

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

}
