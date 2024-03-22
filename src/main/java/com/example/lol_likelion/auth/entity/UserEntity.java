package com.example.lol_likelion.auth.entity;

import com.example.lol_likelion.user.entity.Badge;
import com.example.lol_likelion.user.entity.Follow;
import com.example.lol_likelion.user.entity.Quest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


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
    private String dailyGameCount;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Setter
    @OneToMany(mappedBy = "toUser")
    private List<Follow> followers;

    @Setter
    @OneToMany(mappedBy = "fromUser")
    private List<Follow> followings;

    @Setter
    @Builder.Default
    private String roles = "ROLE_USER";

    @Setter
    @OneToOne(mappedBy = "userId", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Quest quest;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_badge",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "badge_id")
    )

    private Set<Badge> badges = new HashSet<>();

    @Setter
    private Integer trustScore;
    @Setter
    private Integer level;

}
