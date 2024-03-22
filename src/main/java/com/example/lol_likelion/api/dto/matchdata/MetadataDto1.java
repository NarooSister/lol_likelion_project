package com.example.lol_likelion.api.dto.matchdata;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetadataDto1 {
    private String dataVersion;
    private String matchId;
    //참가자 PUUID list
    private List<String> participants;
}
