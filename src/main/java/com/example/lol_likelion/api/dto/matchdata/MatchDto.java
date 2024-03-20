package com.example.lol_likelion.api.dto.matchdata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchDto {
    private MetadataDto metadata;
    private InfoDto info;
}
