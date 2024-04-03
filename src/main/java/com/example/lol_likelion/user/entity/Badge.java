package com.example.lol_likelion.user.entity;
import com.example.lol_likelion.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String image;

    @OneToMany(mappedBy = "badge", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserBadge> userBadges;

}
