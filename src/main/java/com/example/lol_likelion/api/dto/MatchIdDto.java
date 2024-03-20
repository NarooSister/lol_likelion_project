package com.example.lol_likelion.api.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchIdDto {
    private List<String> matchIdList;
}
