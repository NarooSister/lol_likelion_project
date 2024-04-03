package com.example.lol_likelion.user.service;


import com.example.lol_likelion.api.ApiService;
import com.example.lol_likelion.api.dto.LeagueEntryDTO;
import com.example.lol_likelion.api.dto.MatchIdDto;
import com.example.lol_likelion.api.dto.PuuidDto;
import com.example.lol_likelion.api.dto.SummonerDto;
import com.example.lol_likelion.api.dto.matchdata.MatchDto;
import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.user.dto.QuestDto;
import com.example.lol_likelion.user.dto.UserBadgeDto;
import com.example.lol_likelion.user.entity.*;
import com.example.lol_likelion.user.repository.BadgeRepository;
import com.example.lol_likelion.user.repository.QuestRepository;
import com.example.lol_likelion.user.repository.UserBadgeRepository;
import com.example.lol_likelion.auth.repository.UserRepository;
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
    private final QuestRepository questRepository;
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

    //TODO: userPageUpdate 리팩토링...!
    public void userPageUpdate(String gameName, String tagLine) {
        //유저 가져오기
        Optional<UserEntity> optionalUser = userRepository.findByGameNameAndTagLine(gameName, tagLine);
        if (optionalUser.isEmpty()) {
            return;
        }
        UserEntity user = optionalUser.get();

        //최근 업데이트 된 시간 확인
        LocalDateTime updatedAt = user.getUpdatedAt();
        // LocalDateTime을 UTC 기준으로 Unix timestamp로 변환
        Long unixUpdatedAt = updatedAt.toEpochSecond(ZoneOffset.ofHours(9));

        //최근 update 시간 이후의 전적 matchId 가져 오기
        MatchIdDto matchIdDto = apiService.callRiotApiMatchIdByTime(user.getPuuid(), unixUpdatedAt);
        if(matchIdDto == null){
            return;
        }
        System.out.println(matchIdDto);

        QuestDto questDto = new QuestDto();

        //matchDto 안의 size 만큼의 경기 동안의 내 정보 가져오는 과정
        PuuidDto puuidDto = apiService.callRiotApiPuuid(gameName, tagLine);
        SummonerDto summonerDto = apiService.callRiotApiSummonerId(puuidDto);
        List<String> matchIdList = matchIdDto.getMatchIdList();
        //최근 5게임 matchDto를 담을 List
        List<MatchDto> matchDtoList = new ArrayList<>();
        //매 경기 내 정보(participantDto 중 내 puuid와 같은 내용)만 담을 List
        List<MatchDto.InfoDto.ParticipantDto> participantDtoList = new ArrayList<>();

        LeagueEntryDTO leagueEntryDTO = apiService.getSummonerLeagueEntry(summonerDto);

        // 여기에서 matchIdList가 비어 있는지 확인
        if (!matchIdList.isEmpty()) {
            for (String matchId : matchIdList) {
                //가져온 matchId로 인게임 match 정보 가져오기
                MatchDto matchDto = apiService.callRiotApiMatch(matchId);
                if(matchDto != null){
                    //i번 째 matchDto를 matchDtoList에 담음
                    matchDtoList.add(matchDto);
                    //i번째 경기 중 내 정보를 담음
                    MatchDto.InfoDto.ParticipantDto participantDto = apiService.myInfoFromParticipants(matchDto, puuidDto);
                    if(participantDto != null){
                        participantDtoList.add(participantDto);
                    }
                }
            }
        }
        if (!participantDtoList.isEmpty()) {
            // ParticipantDto에서 정보를 추출하여 QuestDto에 저장하는 로직
            for (MatchDto.InfoDto.ParticipantDto participantDto : participantDtoList) {
                //나중에 연승 로직 체크.. 10연승 후 1패 하고 업데이트 시 뱃지 수여 안됨.
                if (participantDto.getWin()) {
                    questDto.setWinning(questDto.getWinning() + 1);
                    questDto.setWinningStreak(questDto.getWinningStreak() + 1);
                } else {
                    questDto.setWinningStreak(0);
                }
                questDto.setTriplekillCount(questDto.getTriplekillCount() + participantDto.getTripleKills());
                questDto.setQuadrakillCount(questDto.getQuadrakillCount() + participantDto.getQuadraKills());
                questDto.setPentakillCount(questDto.getPentakillCount() + participantDto.getPentaKills());
                questDto.setVisionWardPlaced(questDto.getVisionWardPlaced() + participantDto.getWardsPlaced());
                questDto.setWardsTakedowns(questDto.getWardsTakedowns() + participantDto.getWardsKilled());
                if (participantDto.getFirstBloodKill()) {
                    questDto.setFirstBloodCount(questDto.getFirstBloodCount() + 1);
                }
            }
            //questDto에서 정보 추출하여 quest entity에 저장
            Quest quest = getQuest(user, questDto);
            questRepository.save(quest);

            //뱃지 수여 조건 확인 후 뱃지 부여
            checkAndAwardBadges(user, questDto);

            //뱃지 갯수에 따라 level 업데이트 하기
            levelUpdate(user);

            // UserEntity 업데이트 (필요한 경우)
            user.setQuest(quest); // UserEntity에 Quest 설정
            if (!participantDtoList.isEmpty()) {
                user.setPuuid(participantDtoList.get(0).getPuuid());
            }
            user.setTier(leagueEntryDTO.getTier());
            user.setLeagueWins(leagueEntryDTO.getWins());
            user.setLeagueLosses(leagueEntryDTO.getLosses());
            user.setUpdatedAt(LocalDateTime.now());

            userRepository.save(user); // UserEntity 업데이트
        }
    }

    private Quest getQuest(UserEntity user, QuestDto questDto) {
        Quest quest = user.getQuest();
        if (quest == null) {
            quest = new Quest();
            quest.setUser(user); // Quest와 UserEntity 간의 연결 설정
        }
        // QuestDto에서 정보를 추출하여 Quest 엔티티 업데이트
        quest.setWinningStreak(questDto.getWinningStreak());
        quest.setWinning(questDto.getWinning());
        quest.setTriplekillCount(questDto.getTriplekillCount());
        quest.setQuadrakillCount(questDto.getQuadrakillCount());
        quest.setPentakillCount(questDto.getPentakillCount());
        quest.setVisionWardPlaced(questDto.getVisionWardPlaced());
        quest.setWardsTakedowns(questDto.getWardsTakedowns());
        quest.setFirstBloodCount(questDto.getFirstBloodCount());
        return quest;
    }

    public void checkAndAwardBadges(UserEntity user, QuestDto questDto) {

        //신뢰점수 20점 달성 : "우리들의 친절한 이웃"
        if (user.getTrustScore() >= 20) {
            awardTrustBadge(user, "우리들의 친절한 이웃");
        }
        //신뢰점수 -10점 이하
        if (user.getTrustScore() <= -10) {
            awardTrustBadge(user, "비매너 유저");
        }

        if (questDto.getWinningStreak() >= 7) {
            awardBadge(user, "성실한 소환사");
        }
        if (questDto.getWinningStreak() >= 30) {
            awardBadge(user, "진정한 소환사");
        }
        if (questDto.getWinning() >= 10) {
            awardBadge(user, "이왜진?");
        }
        if (questDto.getWinning() >= 100) {
            awardBadge(user, "100점 만점에 100점");
        }
        if (questDto.getTriplekillCount() >= 10) {
            awardBadge(user, "트리플 악셀도 잘하시나요?");
        }
        if (questDto.getQuadrakillCount() >= 5) {
            awardBadge(user, "펜타 놓치기 장인");
        }
        if (questDto.getPentakillCount() >= 3) {
            awardBadge(user, "학살자");
        }
        if (questDto.getVisionWardPlaced() >= 50) {
            awardBadge(user, "광명의 인도자");
        }
        if (questDto.getWardsTakedowns() >= 50) {
            awardBadge(user, "어둠의 인도자");
        }
        if (questDto.getFirstBloodCount() >= 10) {
            awardBadge(user, "400골드 벌고 시작합시다");
        }

    }

    //뱃지 부여하고 저장하는 과정
    public void awardBadge(UserEntity user, String badgeName) {
        //뱃지 이름으로 뱃지 조회
        Badge badge = badgeRepository.findByName(badgeName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "뱃지를 찾을 수 없습니다."));

        // 이미 부여된 뱃지인지 확인
        boolean alreadyAwarded = userBadgeRepository.existsByUserIdAndBadgeId(user.getId(), badge.getId());
        if (!alreadyAwarded) {
            // 사용자에게 뱃지 부여
            UserBadge userBadge = new UserBadge();
            userBadge.setUser(user);
            userBadge.setBadge(badge);
            userBadge.setState(BadgeState.ACQUIRED);
            userBadgeRepository.save(userBadge);
        }
    }

    //신뢰 뱃지 부여하는 과정
    public void awardTrustBadge(UserEntity user, String badgeName) {
        //뱃지 이름으로 뱃지 조회
        Badge badge = badgeRepository.findByName(badgeName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "뱃지를 찾을 수 없습니다."));

        // 부여된 신뢰 뱃지 확인
        List<UserBadge> trustBadge = userBadgeRepository.findAllByUserIdAndState(user.getId(), BadgeState.TRUST);

        //이미 부여된 신뢰 뱃지가 있다면 초기화
        for (UserBadge userBadge : trustBadge) {
            userBadge.setState(BadgeState.NOT_ACQUIRED);
            userBadgeRepository.save(userBadge);
        }

        // 사용자에게 새로운 신뢰 뱃지 부여
        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setState(BadgeState.TRUST);
        userBadgeRepository.save(userBadge);
    }


    // READ ALL - 유저의 모든 뱃지 보여주기
    public List<UserBadgeDto> readAllBadge(UserEntity user) {
        List<UserBadgeDto> userBadgeDtos = new ArrayList<>();
        for (UserBadge userBadge : userBadgeRepository.findAllByUserId(user.getId())) {
            userBadgeDtos.add(UserBadgeDto.fromEntity(userBadge));
        }
        return userBadgeDtos;

    }
    // READ ALL - 유저의 뱃지 중 획득한 뱃지만 보여주기(TRUST 뱃지 제외)
    public List<UserBadgeDto> readAllBadgeExceptTrust(UserEntity user){
        List<UserBadgeDto> userBadgeDtos = new ArrayList<>();
        //획득한 뱃지와 대표 뱃지를 모두 보여줌
        for (UserBadge userBadge : userBadgeRepository.findAllByUserIdAndStates(user.getId(), BadgeState.ACQUIRED, BadgeState.REPRESENTATIVE)) {
            userBadgeDtos.add(UserBadgeDto.fromEntity(userBadge));
        }
        return userBadgeDtos;
    }

    //대표 뱃지 보여주기
    public List<UserBadgeDto> readAllRepresentBadge(UserEntity user) {
        List<UserBadgeDto> userBadgeDtos = new ArrayList<>();
        for (UserBadge userBadge : userBadgeRepository.findAllByUserIdAndState(user.getId(), BadgeState.REPRESENTATIVE)) {
            userBadgeDtos.add(UserBadgeDto.fromEntity(userBadge));
        }
        return userBadgeDtos;
    }

    //대표 뱃지 설정하기
    public void setRepresentBadge(UserEntity user, Long badgeId1, Long badgeId2) {
        //모든 대표 뱃지의 상태 초기화
        List<UserBadge> representBadge = userBadgeRepository.findAllByUserIdAndState(user.getId(), BadgeState.REPRESENTATIVE);
        for (UserBadge userBadge : representBadge) {
            userBadge.setState(BadgeState.ACQUIRED);
            userBadgeRepository.save(userBadge);
        }

        //유저가 선택한 뱃지를 대표 뱃지로 설정
        UserBadge newSelectBadge1 = userBadgeRepository.findByUserIdAndBadgeId(user.getId(), badgeId1);
        UserBadge newSelectBadge2 = userBadgeRepository.findByUserIdAndBadgeId(user.getId(), badgeId2);

        //상태 변경, 저장
        newSelectBadge1.setState(BadgeState.REPRESENTATIVE);
        newSelectBadge2.setState(BadgeState.REPRESENTATIVE);
        userBadgeRepository.save(newSelectBadge1);
        userBadgeRepository.save(newSelectBadge2);
    }

    //뱃지 갯수에 따라 level update 하기
    public void levelUpdate(UserEntity user) {
        //뱃지 갯수 가져오기
        Integer badgeCount = userBadgeRepository.countAllByUserId(user.getId());
        user.setLevel(badgeCount / 2);
    }

}
