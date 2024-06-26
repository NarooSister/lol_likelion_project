package com.example.lol_likelion.duo.entity;

import com.example.lol_likelion.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String memo;

    @Setter
    private Long userId;

    @Setter
    private String myPosition;
    @Setter
    private String findPosition;
    @Setter
    private String status;
    @Setter
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Offer> offer;

    //TODO 추 후 User entity 완료되면 관계성 연결
    @Setter
    // userEntity 필드 추가
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;



}
