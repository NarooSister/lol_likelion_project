package com.example.lol_likelion.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateGameNameDto {
    @NotBlank(message = "소환사 닉네임은 필수입니다")
    private String gameName;
    @NotBlank(message = "게임 태그는 필수입니다.")
    private String tagLine;
}
