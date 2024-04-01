package com.example.lol_likelion.user;


import com.example.lol_likelion.api.ApiService;
import com.example.lol_likelion.api.dto.MatchIdDto;
import com.example.lol_likelion.api.dto.PuuidDto;
import com.example.lol_likelion.api.dto.matchdata.MatchDto;
import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.user.dto.QuestDto;
import com.example.lol_likelion.user.entity.BadgeState;
import com.example.lol_likelion.user.repository.BadgeRepository;
import com.example.lol_likelion.user.repository.UserBadgeRepository;
import com.example.lol_likelion.auth.repository.UserRepository;
import com.example.lol_likelion.user.entity.Badge;
import com.example.lol_likelion.user.entity.UserBadge;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final ApiService apiService;

    // user-page에서 업데이트 버튼을 누르면 일어나는 과정
    // userEntity의 최근 업데이트 된 시간 체크 해서, 그 이후의 최근 전적에서 quest 가져와서 숫자 +
    // 뱃지 조건 달성 확인한 뒤 새로 획득한 뱃지 있으면 상태 저장
    // userEntity안의 tier, dailyGameCount, profileIconId 수정
    // updatedAt 현재 시간으로 변경
    // redirect로 최근 전적 다시 가져옴.


    // 최근 전적 가져와서 사용자 정보 업데이트 하면서 뱃지 검증 시작
    // 획득 조건 달성 확인 후 저장하기
    //EX) 칭찬 뱃지


    public void userPageUpdate(String gameName, String tagLine){
        //유저 가져오기
        Optional<UserEntity> optionalUser = userRepository.findByGameNameAndTagLine(gameName, tagLine);
        if(optionalUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.");
        }
        UserEntity user = optionalUser.get();

        //최근 업데이트 된 시간 확인
        LocalDateTime updatedAt = user.getUpdatedAt();
        // LocalDateTime을 UTC 기준으로 Unix timestamp로 변환
        Long unixUpdatedAt = updatedAt.toEpochSecond(ZoneOffset.ofHours(9));

        //최근 update 시간 이후의 전적 matchId 가져 오기
        MatchIdDto matchIdDto = apiService.callRiotApiMatchIdByTime(user.getPuuid(), unixUpdatedAt);

        QuestDto questDto = new QuestDto();

        //matchDto 안의 size 만큼의 경기 동안의 내 정보 가져오는 과정
        PuuidDto puuidDto = apiService.callRiotApiPuuid(gameName, tagLine);
        List<String> matchIdList = matchIdDto.getMatchIdList();
        //최근 5게임 matchDto를 담을 List
        List<MatchDto> matchDtoList = new ArrayList<>();
        //매 경기 내 정보(participantDto 중 내 puuid와 같은 내용)만 담을 List
        List<MatchDto.InfoDto.ParticipantDto> participantDtoList = new ArrayList<>();

        for (int i = 0; i < matchIdList.size(); i++) {
            //한 개의 matchId 가져오기
            String matchId = matchIdList.get(i);
            //가져온 matchId로 인게임 match 정보 가져오기
            MatchDto matchDto = apiService.callRiotApiMatch(matchId);
            //i번 째 matchDto를 matchDtoList에 담음
            matchDtoList.add(matchDto);
            //i번째 경기 중 내 정보를 담음
            MatchDto.InfoDto.ParticipantDto participantDto = apiService.myInfoFromParticipants(matchDto, puuidDto);
            participantDtoList.add(participantDto);
        }

    }


    //배지 부여하여 저장하는 과정 메소드로 추출
    public void awardBadgeToUser(UserEntity user, Badge badge, BadgeState state){
        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setState(state);
        userBadgeRepository.save(userBadge);
    }
    public void trustBadge(Long userId){
        // -뱃지 Id 부분 하드코딩 어떻게 고칠지 고민..
        //아이디 존재하는지 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        Badge niceBadge = badgeRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("뱃지를 찾을 수 없습니다."));
        Badge badBadge = badgeRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("뱃지를 찾을 수 없습니다."));

        //뱃지를 이미 획득했는지 확인
        boolean niceBadgeAwarded = userBadgeRepository.existsByUserIdAndBadgeId(userId, 1L);
        boolean badBadgeAwarded = userBadgeRepository.existsByUserIdAndBadgeId(userId, 2L);

        // 칭찬 점수 가져오기
        Integer trustScore = user.getTrustScore();
        System.out.println("칭찬 점수: " + trustScore);
        
        if(trustScore>= 20 && niceBadgeAwarded ){
            // 20점이 넘는 경우 친절한 뱃지 부여
            awardBadgeToUser(user, niceBadge, BadgeState.ACQUIRED);
        } else if (trustScore <= -10 && badBadgeAwarded) {
            // -10점 이하인 경우 나쁜 뱃지 부여
            awardBadgeToUser(user, badBadge, BadgeState.ACQUIRED);
        }
    }






    // READ ALL - 모든 뱃지 보여주기

    //대표 뱃지 보여주기

    //대표 뱃지 설정하기

    //신뢰 점수에 따른 칭찬 뱃지 설정하기

    //뱃지 갯수에 따라 level update 하기







}
