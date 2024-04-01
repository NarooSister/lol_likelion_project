package com.example.lol_likelion.auth.dto;

import com.example.lol_likelion.auth.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserDto {
    @NotBlank(message = "로그인 아이디가 비어있습니다.")
    private String username;
    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
    private String passwordCheck;
    @NotBlank(message = "tagLine이 비어있습니다.")
    private String tagLine;
    @NotBlank(message = "라이엇 닉네임이 비어있습니다.")
    private String gameName;
    @NotBlank(message = "이메일이 비어있습니다.")
    private String email;
    @NotBlank(message = "전화번호가 비어있습니다.")
    private String phone;

    private String tier;
    private String puuid;
    private Integer profileIconId;
    public UserEntity toEntity(String encodedPassword
            , String tier, String puuid, Integer profileIconId
    ) {
        return UserEntity.builder()
                .username(this.username)
                .password(encodedPassword)
                .tagLine(this.tagLine)
                .gameName(this.gameName)
                .email(this.email)
                .phone(this.phone)
                .tier(tier)
                .roles("ROLE_USER")
                .puuid(puuid)
                .profileIconId(profileIconId)
                .build();
    }

}
