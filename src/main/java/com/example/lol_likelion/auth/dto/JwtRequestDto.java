package com.example.lol_likelion.auth.dto;

import lombok.*;


@Data
@NoArgsConstructor
public class JwtRequestDto {
    private String username;
    private String password;
}
