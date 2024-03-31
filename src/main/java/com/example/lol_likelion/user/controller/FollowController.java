package com.example.lol_likelion.user.controller;

import com.example.lol_likelion.auth.dto.CustomUserDetails;
import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.auth.repository.UserRepository;
import com.example.lol_likelion.auth.service.UserService;
import com.example.lol_likelion.user.entity.Follow;
import com.example.lol_likelion.user.repository.FollowRepository;
import com.example.lol_likelion.user.service.FollowService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    /*
    @PostMapping("/follow/{id}")
    public String follow(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Optional<UserEntity> optionalFollowing = userRepository.findById(id);
        UserEntity following = optionalFollowing.get();
        UserEntity follower = userDetails.getEntity();

        Follow follow = new Follow();
        follow.setFollowing(following);
        follow.setFollower(follower);

        followRepository.save(follow);

        System.out.println(" follow 완료 ");
        return "follow ok";

    }

    @PostMapping("/unFollow/{id}")
    public String unFollow(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Optional<UserEntity> optinalFollowing = userRepository.findById(id);
        UserEntity following = optinalFollowing.get();
        UserEntity follower = userDetails.getEntity();

        followRepository.deleteByFollowerIdAndFollowingId(follower.getId(), following.getId());

        return "unfollow ok";
    }

     */

    @GetMapping("/users/user-page/{id}")
    public String userDetail(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        //Optional<UserEntity> user0 = userRepository.findById(userDetails.getEntity().getId());
        //System.out.println("userNamed : " + userDetails.getUsername());

        List<Follow> followingList = followRepository.findByFollowingId(id);
        int followingCount = followingList.size();

        // 리스트 목록의 id가 일치하는 gameName 불러와서 목록으로 뿌리기

        List<Follow> followerList = followRepository.findByFollowerId(id);
        int followerCount = followerList.size();

        System.out.println(followingCount);
        return "/users/user-page";

    }



}
