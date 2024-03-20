package com.example.lol_likelion.auth.dto;

import lombok.Data;

@Data
public class JwtRequestDto {
    private String username;
    private String password;
}
