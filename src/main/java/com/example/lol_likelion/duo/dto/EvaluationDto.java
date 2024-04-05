package com.example.lol_likelion.duo.dto;

import com.example.lol_likelion.duo.entity.Evaluation;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDto {

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

    public static EvaluationDto fromEntity(Evaluation entity){
        EvaluationDto.EvaluationDtoBuilder builder = EvaluationDto.builder()
                .id(entity.getId())
                .evaluator(entity.getEvaluator())
                .beingEvaluated(entity.getBeingEvaluated())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .postId(entity.getPostId());
        return builder.build();
    }
}
