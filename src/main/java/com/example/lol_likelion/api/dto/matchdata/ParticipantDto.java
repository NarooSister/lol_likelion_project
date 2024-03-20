package com.example.lol_likelion.api.dto.matchdata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto {
    private String puuid;

    private String riotIdGameName;
    private String riotIdTagline;

    private String championName;

    private String lane;

    private Integer kills;
    private Integer deaths;
    private Integer assists;
    private Integer kda;

    private Integer pentaKills;
    private Integer quadraKills;
    private Integer tripleKills;

    //제어와드 구매횟수
    private Integer visionWardsBoughtInGame;
    //와드 설치 횟수
    private Integer wardsPlaced;
    //와드 파괴 횟수
    private Integer wardsKilled;

    //100 = 레드팀 ,  200 = 블루팀
    private Integer teamId;
    private Boolean win;

}
