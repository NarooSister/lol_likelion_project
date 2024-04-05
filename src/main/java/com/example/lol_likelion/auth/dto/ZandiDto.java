package com.example.lol_likelion.auth.dto;

import com.example.lol_likelion.user.entity.Zandi;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ZandiDto {
    private Long id;
    @Setter
    private Integer userId;
    @Setter
    private Integer gameCount;
    @Setter
    private LocalDateTime createAt;

    public static ZandiDto fromEntity(Zandi entity) {
        return new ZandiDto(
                entity.getId(),
                entity.getUserId(),
                entity.getGameCount(),
                entity.getCreateAt()
        );
    }
}
