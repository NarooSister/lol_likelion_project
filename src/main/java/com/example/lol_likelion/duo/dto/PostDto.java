package com.example.lol_likelion.duo.dto;

import com.example.lol_likelion.duo.entity.Offer;
import com.example.lol_likelion.duo.entity.Post;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Integer id;
    private String memo;
    private String userId;
    private String myPosition;
    private String findPosition;
    private String status;
    private LocalDateTime createdAt;
    private List<Offer> offer;

    //TODO 권한 완료하면 권한에 부여 해야 함.
    public static PostDto fromEntity(Post entity){
        PostDtoBuilder builder = PostDto.builder()
                .id(entity.getId())
                .memo(entity.getMemo())
                .userId(entity.getUserId())
                .myPosition(entity.getMyPosition())
                .findPosition(entity.getFindPosition())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .offer(entity.getOffer());

        return builder.build();
    }
}
