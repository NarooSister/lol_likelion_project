package com.example.lol_likelion.auth.entity;

import com.example.lol_likelion.user.entity.Follow;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

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
}
