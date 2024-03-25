package com.example.lol_likelion.auth.service;

import com.example.lol_likelion.api.ApiService;
import com.example.lol_likelion.api.dto.PuuidDto;
import com.example.lol_likelion.api.dto.SummonerDto;
import com.example.lol_likelion.auth.dto.JwtRequestDto;
import com.example.lol_likelion.auth.dto.CreateUserDto;
import com.example.lol_likelion.auth.dto.CustomUserDetails;
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
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApiService apiService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationFacade authenticationFacade;

    //존재하는 아이디인지 확인
    public boolean checkUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    //존재하는 소환사 닉네임인지 확인
    public boolean checkGameName(String tagLine, String gameName){
        return userRepository.existsByTagLineAndGameName(tagLine, gameName);
    }

    //Riot Api를 통해 실제로 있는 소환사 아이디인지 확인
    public boolean riotApiCheckGameName(String tagLine, String gameName) {
        //puuid 가져오기
        PuuidDto puuidDto = apiService.callRiotApiPuuid(gameName, tagLine);
        //puuid로 소환사 닉네임 가져오기
        SummonerDto summonerDto = apiService.callRiotApiSummonerId(puuidDto);

        return summonerDto.getId().equals(gameName);
    }

    //회원가입
    public void createUser(CreateUserDto dto) {
        userRepository.save(dto.toEntity(passwordEncoder.encode(dto.getPassword())));
    }

    //로그인
    public UserEntity login(JwtRequestDto dto) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(dto.getUsername());
        if (optionalUser.isEmpty()) {
            return null;
        }
        //비밀번호 확인
        UserEntity user = optionalUser.get();
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return null;
        }

        return user;
    }

    //my-page (구현중)
    //username을 입력받아 UserEntity를 return
    //인증, 인가에 사용 가능
    public UserEntity getUserByUsername(String username) {
        if (username == null) return null;
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElse(null);
    }

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
