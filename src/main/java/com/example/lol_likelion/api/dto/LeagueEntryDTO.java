package com.example.lol_likelion.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LeagueEntryDTO {
    private String leagueId;
    private String tier;
    private String rank;
    private String leaguePoints;
    private Integer wins;
    private Integer losses;
}
