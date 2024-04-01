package com.example.lol_likelion.api.dto.matchdata;
import lombok.*;

import java.util.List;


@Data
public class MatchDto {
    public MatchDto() {
    }
    private MetadataDto metadata;
    private InfoDto info;
    @Getter
    @Setter
    public static class MetadataDto {
        private String dataVersion;
        private String matchId;
        //참가자 PUUID list
        private List<String> participants;
    }
    @Getter
    @Setter
    public static class InfoDto {
        private String gameVersion;
        private Long gameEndTimestamp;
        private Long gameStartTimestamp	;
        //게임 플레이 시간
        private Long gameDuration;
        private List<ParticipantDto> participants;
        @Getter
        //참가자 정보
        public static class ParticipantDto {
            private String puuid;
            private String riotIdGameName;
            private String riotIdTagline;
            private String championName;
            private Integer profileIcon;

            private String lane;

            private Integer kills;
            private Integer deaths;
            private Integer assists;
            private Double kda;
            private Integer totalMinionsKilled;
            private Integer pentaKills;
            private Integer quadraKills;
            private Integer tripleKills;

            private Boolean firstBloodKill;

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
    }
}
