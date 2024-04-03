package com.example.lol_likelion.user.service;

import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.auth.repository.UserRepository;
import com.example.lol_likelion.auth.service.UserService;
import com.example.lol_likelion.user.dto.UserProfileDto;
import com.example.lol_likelion.user.entity.Follow;
import com.example.lol_likelion.user.repository.FollowJPARepository;
import com.example.lol_likelion.user.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final FollowJPARepository followJPARepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public UserProfileDto userProfile(Long pageUserId, Long principalId) throws Exception{
        // 해당 페이지 주인의 ID 를 받아준다

        UserProfileDto dto = new UserProfileDto();

        dto.setPageOwnerState(pageUserId.equals(principalId)); // 1은 페이지 주인 , -1 주인이 아님

        // dto에 follow 정보를 담기
        boolean followState = followJPARepository.existsByFollowerIdAndFollowingId(principalId, pageUserId);
        Long followingCount = followRepository.followingCount(pageUserId);
        Long followerCount = followRepository.followerCount(pageUserId);

        dto.setFollowState(followState);
        dto.setFollowingCount(followingCount);
        dto.setFollowerCount(followerCount);
        dto.setFollowingUser(pageUserId);
        dto.setFollowerUser(principalId);

        System.out.println("페이지 주인 : "+ dto.isPageOwnerState());
        System.out.println("팔로우 여부: " + followState);
        System.out.println("찾는 사용자 : " + pageUserId);
        System.out.println("팔로워 수 : " + followerCount);
        System.out.println("팔로잉 수: " + followingCount);
        System.out.println("followerId : " + pageUserId);
        System.out.println("followingId: " + principalId);

        return dto;

    }
  
   // FOLLOW
    public void follow(Long userPageId, Long followerId){

        UserProfileDto dto = new UserProfileDto();

        Follow follow = new Follow();

        // 팔로우 할 사람 => pageUserId
        UserEntity user1 = userService.findUserById(userPageId);
        // 팔로우하는 사람 => 로그인한 사람
        UserEntity user2 = userService.findUserById(followerId);

        follow.setFollowing(user1);
        follow.setFollower(user2);
        follow.setFollowingUser(user1.getId());
        follow.setFollowerUser(user2.getId());

        followJPARepository.save(follow);
    }

    // UNFOLLOW
    public void unfollow(Long userPageId, Long followerId){
        System.out.println("userPageId = " + userPageId);
        System.out.println("followerId = " + followerId);

        Optional<Follow> follow = followJPARepository.findByFollowerIdAndFollowingId(followerId, userPageId);
        followJPARepository.deleteById(follow.get().getId());
    }

    public List<Follow> findFollowersByUserId(Long userId) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            return userEntity.getFollowerList();
        } else {
            throw new RuntimeException("유저 아이디를 찾을 수 없습니다.");
        }
    }
    public List<Follow> findFollowingByUserId(Long userId) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            return userEntity.getFollowingList();
        } else {
            throw new RuntimeException("유저 아이디를 찾을 수 없습니다.");
        }
    }


}