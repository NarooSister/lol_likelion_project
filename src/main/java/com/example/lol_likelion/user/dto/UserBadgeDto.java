package com.example.lol_likelion.user.dto;

import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.user.entity.Badge;
import com.example.lol_likelion.user.entity.BadgeState;
import com.example.lol_likelion.user.entity.UserBadge;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
public class UserBadgeDto {
    private UserEntity user;
    private Badge badge;
    private BadgeState state;
    private LocalDateTime createdAt;

    public static UserBadgeDto fromEntity(UserBadge entity){
       return UserBadgeDto.builder()
               .user(entity.getUser())
               .badge(entity.getBadge())
               .state(entity.getState())
               .createdAt(entity.getCreatedAt())
               .build();
    }
}
