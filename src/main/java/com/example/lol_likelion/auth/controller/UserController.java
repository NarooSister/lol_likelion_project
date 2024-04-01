package com.example.lol_likelion.auth.controller;

import com.example.lol_likelion.auth.dto.*;
import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.auth.jwt.JwtTokenUtils;
import com.example.lol_likelion.auth.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {

    //에러 메시지 표시하기 위해 controller단에서 검증

    private final UserService service;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping("/")
    public String mainForm(Model model){

        //로그인 된 유저인지 확인하기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null &&
                !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);

        return "main";
    }

    @GetMapping("/register")
    public String signUpForm(Model model) {
        model.addAttribute("createUserDto", new CreateUserDto());
        return "register";
    }

    @PostMapping("/register")
    public String create(@Valid CreateUserDto dto, BindingResult bindingResult)
    {
        //Username 중복 체크
        if(service.checkUsername(dto.getUsername())) {
            bindingResult.addError(new FieldError("dto", "username", "로그인 아이디가 중복됩니다."));
        }

        //소환사 닉네임 중복 검증
        if (service.checkGameName(dto.getGameName(), dto.getTagLine())){
            bindingResult.addError(new FieldError("dto", "gameName", "이미 가입된 소환사 닉네임 입니다."));
        }

        //소환사 닉네임 검증
        if (!service.riotApiCheckGameName(dto.getGameName(), dto.getTagLine())){
            bindingResult.addError(new FieldError("dto", "tagLine", "태그를 바르게 입력해 주십시오. (Ex. KR1)"));
            bindingResult.addError(new FieldError("dto", "gameName", "실제로 존재하는 소환사 닉네임을 입력해 주십시오."));
        }

        //password와 passwordCheck 검증
        if(!dto.getPassword().equals(dto.getPasswordCheck())) {
            bindingResult.addError(new FieldError("dto", "passwordCheck", "비밀번호가 일치하지 않습니다."));
        }

        if(bindingResult.hasErrors()) {
            return "register";
        }

        service.createUser(dto);
        return "register-success";
    }

    @GetMapping("/login")
    public String loginForm(Model model)
    {
        model.addAttribute("jwtRequestDto", new JwtRequestDto());
        return "login";
    }
    @PostMapping("/login")
    private String login(@ModelAttribute JwtRequestDto jwtRequestDto, BindingResult bindingResult, HttpServletResponse response)
    {
        UserEntity user = service.login(jwtRequestDto);
        //아이디나 비밀번호가 틀린 경우 Error
        if(user == null){
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 틀렸습니다.");
        }
        if(bindingResult.hasErrors()){
            return "login";
        }

        //로그인 성공 -> jwt token 발급
        String token = jwtTokenUtils.generateToken(CustomUserDetails.fromEntity(user));

        // 발급한 Jwt Token을 Cookie를 통해 전송
        // 클라이언트는 다음 요청부터 Jwt Token이 담긴 쿠키 전송 => 이 값을 통해 인증, 인가 진행
        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 10);  // 쿠키 유효 시간 : 1시간
        response.addCookie(cookie);

        return "redirect:/main";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // 쿠키 파기
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "main";
    }

    @GetMapping("/my-page")
    public String myPage(Model model, Authentication authentication){
        UserEntity user = service.getUserByUsername(authentication.getName());
        model.addAttribute("user", user);

        //로그인 된 유저인지 확인하기
        authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null &&
                !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);

        return "my-page";
    }

    @GetMapping("/authentication-fail")
    public String authenticationFail(){
        return "authentication-fail";
    }

    @GetMapping("/authorization-fail")
    public String authorizationFail(){
        return "authorization-fail";
    }


    @PostMapping("/users")
    public String updateUser(Model model, UpdateUserDto user){
        service.updateUser(user);
        model.addAttribute("user", user);
        return "redirect:/my-page";
    }
    @GetMapping("/users/password")
    public String updatePasswordForm(Model model){
        model.addAttribute("updatePasswordDto", new UpdatePasswordDto());
        return "password-update";
    }
    @PostMapping("/users/password")
    public String updatePassword(@Valid UpdatePasswordDto dto, BindingResult bindingResult) {

        // 현재 비밀번호와 입력된 비밀번호가 일치하는지
        if (!service.checkCurrentPassword(dto.getCurrentPassword())) {
            bindingResult.addError(new FieldError("dto", "currentPassword", "현재 비밀번호가 올바르지 않습니다."));
        }
        //새 비밀번호와 비밀번호 확인이 일치하는지
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            bindingResult.addError(new FieldError("dto", "confirmPassword", "새 비밀번호가 일치하지 않습니다."));
        }

        if (bindingResult.hasErrors()) {
            return "password-update";
        }

        // 비밀번호 변경 로직 실행
        service.updatePassword(dto);
        return "redirect:/my-page";
    }

    @GetMapping("/users/game-name")
    public String updateGameNameForm(Model model){
        model.addAttribute("updateGameNameDto", new UpdateGameNameDto());
        return "gameName-update";
    }
    @PostMapping("/users/game-name")
    public String updateGameName(@Valid UpdateGameNameDto dto, BindingResult bindingResult){

        //소환사 닉네임 중복 검증
        if (service.checkGameName(dto.getGameName(), dto.getTagLine())){
            bindingResult.addError(new FieldError("dto", "gameName", "이미 가입된 소환사 닉네임 입니다."));
        }

        //소환사 닉네임 검증
        if (!service.riotApiCheckGameName(dto.getGameName(), dto.getTagLine())){
            bindingResult.addError(new FieldError("dto", "tagLine", "태그를 바르게 입력해 주십시오. (Ex. KR1)"));
            bindingResult.addError(new FieldError("dto", "gameName", "실제로 존재하는 소환사 아이디를 입력해 주십시오."));
        }

        if(bindingResult.hasErrors()) {
            return "gameName-update";
        }

        service.updateGameName(dto);
        return "redirect:/my-page";
    }


}
