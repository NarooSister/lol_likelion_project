package com.example.lol_likelion.user.dto;

import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.user.entity.Quest;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestDto {
    private LocalDate lastPlayDate;
    private Integer continuousDailyGame=0;
    private Integer winningStreak=0;
    private Integer winning=0;
    private Integer triplekillCount=0;
    private Integer quadrakillCount=0;
    private Integer pentakillCount=0;
    private Integer visionWardPlaced=0;
    private Integer wardsTakedowns=0;
    private Integer firstBloodCount=0;

 /*   public static QuestDto fromEntity(UserEntity userEntity, Quest questEntity){
        return QuestDto.builder()
                .dailyGameCount(userEntity.getDailyGameCount())
                .trustScore(userEntity.getTrustScore())
                .level(userEntity.getLevel())
                .continuousDailyGame(questEntity.getContinuousDailyGame())
                .winningStreak(questEntity.getWinningStreak())
                .winning(questEntity.getWinning())
                .triplekillCount(questEntity.getTriplekillCount())
                .quadrakillCount(questEntity.getQuadrakillCount())
                .pentakillCount(questEntity.getPentakillCount())
                .visionWardPlaced(questEntity.getVisionWardPlaced())
                .wardsTakedowns(questEntity.getWardsTakedowns())
                .firstBloodCount(questEntity.getFirstBloodCount())
                .build();
    }*/

}
