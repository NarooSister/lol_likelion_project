package com.example.lol_likelion.user.service;

import com.example.lol_likelion.auth.dto.UserInfoDto;
import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.auth.repository.UserRepository;
import com.example.lol_likelion.auth.service.UserService;
import com.example.lol_likelion.user.dto.UserProfileDto;
import com.example.lol_likelion.user.entity.Follow;
import com.example.lol_likelion.user.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserProfileDto userProfile(Long pageUserId, Long principalId) throws Exception{
        // 해당 페이지 주인의 ID 를 받아준다

        UserProfileDto dto = new UserProfileDto();

        UserEntity userEntity = userRepository.findById(pageUserId).orElseThrow(()-> {
            new Exception("해당 프로필 페이지는 없는 페이지입니다.");
            return null;
        });

        dto.setPageOwnerState(pageUserId.equals(principalId)); // 1은 페이지 주인 , -1 주인이 아님

        // dto에 follow 정보를 담기
        Long followState = followRepository.followState(principalId, pageUserId);
        Long followingCount = followRepository.followingCount(pageUserId);
        Long followerCount = followRepository.followerCount(pageUserId);

        dto.setFollowState(followState == 1);
        dto.setFollowCount(followingCount);
        dto.setFollowCount(followerCount);

        System.out.println("pageUserId: " + pageUserId);
        System.out.println("follower: " + followerCount);
        return dto;

    }




}
