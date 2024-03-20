package com.example.lol_likelion.auth.dto;

import com.example.lol_likelion.auth.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserInfoDto {

    private Long id;
    private String username;
    private String gameName;
    private String tagLine;
    private String email;
    private String phone;

    private List<String> roles;

    public static UserInfoDto fromEntity(UserEntity entity)
    {
       List<String> roles = Arrays.stream(entity.getRoles().split(",")).toList();
        return UserInfoDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .gameName(entity.getGameName())
                .tagLine(entity.getTagLine())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .roles(roles)
                .build();
    }
}
