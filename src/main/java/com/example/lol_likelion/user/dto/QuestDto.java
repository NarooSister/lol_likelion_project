package com.example.lol_likelion.user.dto;

import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.user.entity.Quest;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestDto {
    //UserEntity
    private Integer dailyGameCount;
    private Integer trustScore;
    private Integer level;
    //Quest
    private Integer continuousDailyGame;
    private Integer winningStreak;
    private Integer winning;
    private Integer triplekillCount;
    private Integer quadrakillCount;
    private Integer pentakillCount;
    private Integer visionWardPlaced;
    private Integer wardsTakedowns;
    private Integer firstBloodCount;

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
