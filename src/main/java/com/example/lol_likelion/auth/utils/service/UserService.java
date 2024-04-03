package com.example.lol_likelion.auth.utils.service;

import com.example.lol_likelion.api.ApiService;
import com.example.lol_likelion.api.dto.PuuidDto;
import com.example.lol_likelion.api.dto.SummonerDto;
import com.example.lol_likelion.auth.dto.*;
import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.auth.repository.UserRepository;
import com.example.lol_likelion.auth.utils.AuthenticationFacade;
import com.example.lol_likelion.duo.dto.PostDto;
import com.example.lol_likelion.duo.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service

@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApiService apiService;
    private final AuthenticationFacade authenticationFacade;

    //존재하는 아이디인지 확인
    public boolean checkUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    //존재하는 소환사 닉네임인지 확인
    public boolean checkGameName(String gameName, String tagLine){
        return userRepository.existsByGameNameAndTagLine(gameName, tagLine);
    }


    //Riot Api를 통해 실제로 있는 소환사 아이디인지 확인
    public boolean riotApiCheckGameName(String gameName, String tagLine) {
        try {
            //입력 받은 tagLine과 gameName으로 puuid 가져오기
            PuuidDto puuidDto = apiService.callRiotApiPuuid(gameName, tagLine);
            // puuidDto에 puuid가 잘 들어있으면 존재하는 것으로 간주
            return puuidDto != null && !puuidDto.getPuuid().isEmpty();

        } catch (Exception e) {
            // API 호출 실패 (예: 네트워크 문제, 잘못된 입력 등)
            return false;
        }
    }

    //회원가입
    @Transactional(timeout = 10)
    public void createUser(CreateUserDto dto) {
        //tier 정보 가져와서 저장하기
        //과정 : gameName과 tagLine으로 puuid 가져오기 -> puuid로 summonerId 와 profileIconId 가져오기
        //-> summonerId로 tier 가져오기...
        //프로필 아이콘 주소 : https://ddragon.leagueoflegends.com/cdn/10.6.1/img/profileicon/{profileIconId}.png
        PuuidDto puuidDto = apiService.callRiotApiPuuid(dto.getGameName(), dto.getTagLine());
        SummonerDto summonerDto = apiService.callRiotApiSummonerId(puuidDto);
        String tier = apiService.getSummonerTierName(summonerDto);

        //dto로 받은 유저정보와 tier 정보, profileIconId 저장하기
        userRepository.save(dto.toEntity(passwordEncoder.encode(dto.getPassword()), tier, puuidDto.getPuuid(), summonerDto.getProfileIconId(), LocalDateTime.now()));
    }

    //로그인
    @Transactional
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

    //username을 입력받아 UserEntity를 return
    //인증, 인가에 사용 가능
    @Transactional
    public UserEntity getUserByUsername(String username) {
        if (username == null) return null;
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElse(null);
    }

    //유저 정보 수정하기
    @Transactional
    public void updateUser(UpdateUserDto dto) {
        UserEntity user = authenticationFacade.extractUser();

        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());

        UserInfoDto.fromEntity(userRepository.save(user));
    }

    //현재 비밀번호와 입력된 비밀번호 비교
    public boolean checkCurrentPassword(String password) {
        // 현재 인증된 사용자의 비밀번호 가져오기
        UserEntity user = authenticationFacade.extractUser();
        return passwordEncoder.matches(password, user.getPassword());
    }

    // 새 비밀번호 업데이트
    @Transactional
    public void updatePassword(UpdatePasswordDto dto) {
        UserEntity user = authenticationFacade.extractUser();

        // 새 비밀번호 암호화
        String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(encodedNewPassword);

        // 변경 사항 저장
        userRepository.save(user);
    }

    // 새 소환사 닉네임 업데이트
    // 닉네임 변경에 따른 puuid, 티어, 프로필 아이콘 변경
    // TODO: 소환사 닉네임 바꾸면 뱃지 초기화 해야 할지 ?
    @Transactional
    public void updateGameName(UpdateGameNameDto dto){
        UserEntity user = authenticationFacade.extractUser();

        PuuidDto puuidDto = apiService.callRiotApiPuuid(dto.getGameName(), dto.getTagLine());
        SummonerDto summonerDto = apiService.callRiotApiSummonerId(puuidDto);
        String tier = apiService.getSummonerTierName(summonerDto);

        user.setGameName(dto.getGameName());
        user.setTagLine(dto.getTagLine());
        user.setPuuid(puuidDto.getPuuid());
        user.setTier(tier);
        user.setProfileIconId(summonerDto.getProfileIconId());
        userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(CustomUserDetails::fromEntity)
                .orElseThrow(() -> new UsernameNotFoundException("not found"));
    }

    @Transactional
    public UserEntity findByGameNameAndTagLine(String gameName, String tagLine) {
        Optional<UserEntity> optionalUser = userRepository.findByGameNameAndTagLine(gameName, tagLine);
        return optionalUser.orElse(null);
    }

    public UserEntity findById(Long userId){
        return userRepository.findById(userId).orElseThrow();
    }

    public void updateTrust(Long userId, Integer trustScore){

        UserEntity user = userRepository.findById(userId).orElseThrow();
        int score = 0;
        if (user.getTrustScore() == null){
            score = score + trustScore;
        }else {
            score = user.getTrustScore();
            score = score + trustScore;
        }
        user.setTrustScore(score);

        userRepository.save(user);
    }


}