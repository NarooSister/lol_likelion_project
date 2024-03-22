package com.example.lol_likelion.auth.service;

import com.example.lol_likelion.auth.dto.JwtRequestDto;
import com.example.lol_likelion.auth.dto.JwtResponseDto;
import com.example.lol_likelion.auth.dto.CreateUserDto;
import com.example.lol_likelion.auth.dto.CustomUserDetails;
import com.example.lol_likelion.auth.dto.UpdateUserDto;
import com.example.lol_likelion.auth.dto.UserInfoDto;
import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.auth.jwt.JwtTokenUtils;
import com.example.lol_likelion.auth.repository.UserRepository;
import com.example.lol_likelion.auth.utils.AuthenticationFacade;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationFacade authenticationFacade;

    @Transactional
    public void createUser(CreateUserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이미 존재하는 아이디 입니다.");

        //TODO gameName 확인하기
        //gameName과 tagLine으로 puuid 검색

        //이미 있는 사용자인지 확인

        //DB에 puuid 저장


        UserInfoDto.fromEntity(userRepository.save(UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .tagLine(dto.getTagLine())
                .gameName(dto.getGameName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .roles("ROLE_USER")
                .build()));
    }

    //jwt 토큰 발급
    @Transactional
    public void login(JwtRequestDto dto, HttpServletResponse response) {
        UserEntity userEntity = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유저 정보를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), userEntity.getPassword()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다.");

//        String jwt = jwtTokenUtils.generateToken(CustomUserDetails.fromEntity(userEntity));
//        JwtResponseDto responseDto = new JwtResponseDto();
//        responseDto.setToken(jwt);
//        return responseDto;

        CustomUserDetails userDetails = CustomUserDetails.fromEntity(userEntity);
        // JWT 토큰 생성
        String jwt = jwtTokenUtils.generateToken(userDetails);

        // JWT 토큰을 쿠키에 저장
        Cookie cookie = new Cookie("token", jwt);
        //   cookie.setHttpOnly(true); // XSS 공격 방지
       // cookie.setPath("/"); // 전체 경로에서 쿠키 사용
        cookie.setMaxAge(60 * 60 * 60);
        response.addCookie(cookie);

    }

//    //READ one
//    public UserInfoDto readUser(JwtRequestDto dto){
//        UserEntity userEntity = userRepository.findByUsername(dto.getUsername())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유저 정보를 찾을 수 없습니다."));
//
//        return UserInfoDto.fromEntity(userEntity);
//    }

//    @Transactional
//    public UserInfoDto updateUser(UpdateUserDto dto) {
//        UserEntity userEntity = authenticationFacade.extractUser();
//        String username = userEntity.getUsername();
//        log.info(username);
////        userEntity.setPassword(dto.getPassword());
////        userEntity.setGameName(dto.getGameName());
////        userEntity.setTagLine(dto.getTagLine());
//        userEntity.setPhone(dto.getPhone());
//        userEntity.setEmail(dto.getEmail());
//
//        return UserInfoDto.fromEntity(userRepository.save(userEntity));
//    }
}
