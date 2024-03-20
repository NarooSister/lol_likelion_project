package com.example.lol_likelion.user.entity;
import com.example.lol_likelion.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private Integer state;   //활성상태 미획득 0, 획득 1

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userId;


}
