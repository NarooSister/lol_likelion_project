package com.example.lol_likelion.user.dto;

import com.example.lol_likelion.auth.dto.UserInfoDto;
import com.example.lol_likelion.auth.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserProfileDto {

    private boolean PageOwnerState; // 페이지 주인 여부 - 1: 주인 , -1 : 주인 x
    private UserInfoDto user; // 접속한 유저정보를 받을 유저 오브젝트
    private Long followingUser;
    private Long followerUser;
    private boolean followState; // 팔로우상태 / 했으면 true , 안했으면 false
    private Long followingCount; // 팔로잉수 카운팅
    private Long followerCount; // 팔로워수 카운팅
}
