package com.example.lol_likelion.auth.controller;

import com.example.lol_likelion.auth.dto.JwtRequestDto;
import com.example.lol_likelion.auth.dto.JwtResponseDto;
import com.example.lol_likelion.auth.dto.CreateUserDto;
import com.example.lol_likelion.auth.dto.UpdateUserDto;
import com.example.lol_likelion.auth.dto.UserInfoDto;
import com.example.lol_likelion.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/login")
    public String loginForm()
    {
        return "login-form";
    }

    @GetMapping("/my-profile")
    public String myProfile()
    {
        return "my-profile";
    }

    @PostMapping("/create")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserInfoDto create(@RequestBody CreateUserDto dto)
    {
        return service.createUser(dto);
    }

    @PostMapping("/signin")
    @ResponseBody
    private JwtResponseDto signin(@RequestBody JwtRequestDto dto)
    {
        return service.signin(dto);
    }

    @PostMapping("/update")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserInfoDto update(@RequestBody UpdateUserDto dto)
    {
        return service.updateUser(dto);
    }


}
