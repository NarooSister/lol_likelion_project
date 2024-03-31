package com.example.lol_likelion.user.controller;

import com.example.lol_likelion.auth.dto.CustomUserDetails;
import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.auth.repository.UserRepository;
import com.example.lol_likelion.auth.service.UserService;
import com.example.lol_likelion.user.dto.UserProfileDto;
import com.example.lol_likelion.user.entity.Follow;
import com.example.lol_likelion.user.repository.FollowRepository;
import com.example.lol_likelion.user.service.FollowService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserService userService;


    @GetMapping("/users/user-page/{pageUserId}")
    public String profile(@PathVariable Long pageUserId, Model model, Authentication authentication) throws Exception {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userName = userDetails.getUsername();
        System.out.println("사용자 이름 : " + userName);

        UserEntity userEntity = userService.getUserByUsername(userName);
        Long userId = userEntity.getId();


        UserProfileDto dto = followService.userProfile(pageUserId, userId);

        model.addAttribute("dto", dto);

        System.out.println(dto);

        return "user-page";

    }


}
