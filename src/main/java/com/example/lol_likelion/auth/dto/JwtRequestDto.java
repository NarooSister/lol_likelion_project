package com.example.lol_likelion.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JwtRequestDto {
    private String username;
    private String password;
}
