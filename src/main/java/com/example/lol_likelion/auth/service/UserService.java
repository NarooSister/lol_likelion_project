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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationFacade authenticationFacade;

    @Transactional
    public UserInfoDto createUser(CreateUserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이미 존재하는 아이디 입니다.");

        return UserInfoDto.fromEntity(userRepository.save(UserEntity.builder()
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
    public JwtResponseDto signin(JwtRequestDto dto) {
        UserEntity userEntity = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유저 정보를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), userEntity.getPassword()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다.");

        String jwt = jwtTokenUtils.generateToken(CustomUserDetails.fromEntity(userEntity));
        JwtResponseDto responseDto = new JwtResponseDto();
        responseDto.setToken(jwt);
        return responseDto;
    }

    @Transactional
    public UserInfoDto updateUser(UpdateUserDto dto) {
        UserEntity userEntity = authenticationFacade.extractUser();
        String username = userEntity.getUsername();
        log.info(username);
//        userEntity.setPassword(dto.getPassword());
//        userEntity.setGameName(dto.getGameName());
//        userEntity.setTagLine(dto.getTagLine());
        userEntity.setPhone(dto.getPhone());
        userEntity.setEmail(dto.getEmail());

        return UserInfoDto.fromEntity(userRepository.save(userEntity));
    }
}
