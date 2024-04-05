package com.example.lol_likelion.api.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChampionMasteryDto {
    private Long championId;
    private String puuid;
}
