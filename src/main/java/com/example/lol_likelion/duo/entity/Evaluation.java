package com.example.lol_likelion.duo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    //평가 하는 사람
    private Long evaluator;
    @Setter
    //평가 받는 사람
    private Long beingEvaluated;
    @Setter
    private String status;
    @Setter
    private Long postId;
    @Setter
    @CreationTimestamp
    private LocalDateTime createdAt;

}
