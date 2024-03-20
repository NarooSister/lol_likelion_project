package com.example.lol_likelion.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateUserDto {
    private String username;
    private String password;
    private String tagLine;
    private String gameName;
    private String email;
    private String phone;
}
