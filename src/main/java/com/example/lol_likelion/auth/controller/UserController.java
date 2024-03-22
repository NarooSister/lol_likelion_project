package com.example.lol_likelion.auth.controller;

import com.example.lol_likelion.auth.dto.*;
import com.example.lol_likelion.auth.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    @GetMapping("/register")
    public String signUpForm() {
        return "register";
    }

    @PostMapping("/register")
    public String create(CreateUserDto dto)
    {
        service.createUser(dto);
        return "register-success";
    }

    @GetMapping("/login")
    public String loginForm()
    {
        return "login";
    }
    @PostMapping("/login")
    private String login(JwtRequestDto dto, HttpServletResponse response)
    {
         service.login(dto, response);
        return "main";
    }

//    @GetMapping("/my-page")
//    public String myPage(){
//        return "my-page";
//    }
//
//    @PostMapping("/update")
//    public String update(UpdateUserDto dto, Model model)
//    {
//        service.updateUser(dto);
//        return "redirect:my-page";
//    }

}
