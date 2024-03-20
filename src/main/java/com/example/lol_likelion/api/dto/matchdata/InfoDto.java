package com.example.lol_likelion.api.dto.matchdata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoDto {
    private String gameVersion;
    private Long gameEndTimestamp;
    private Long gameStartTimestamp	;
    //게임 플레이 시간
    private Long gameDuration;
    //참가자 정보
    private List<ParticipantDto> participants;
}
