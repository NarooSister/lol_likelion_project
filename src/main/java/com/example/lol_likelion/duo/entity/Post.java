package com.example.lol_likelion.duo.entity;

import jakarta.persistence.*;
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
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    private String memo;

    @Setter
    private String userId;

    @Setter
    private String myPosition;
    @Setter
    private String findPosition;
    @Setter
    private String status;
    @Setter
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Offer> offer = new ArrayList<>();

    //TODO 추 후 User entity 완료되면 관계성 연결
//    @ManyToOne(fetch = FetchType.LAZY)
//    private User user;



}
