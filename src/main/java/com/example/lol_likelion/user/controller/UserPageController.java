package com.example.lol_likelion.user.controller;

import com.example.lol_likelion.api.ApiService;
import com.example.lol_likelion.api.dto.LeagueEntryDTO;
import com.example.lol_likelion.api.dto.MatchIdDto;
import com.example.lol_likelion.api.dto.PuuidDto;
import com.example.lol_likelion.api.dto.SummonerDto;
import com.example.lol_likelion.api.dto.matchdata.MatchDto;
import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.auth.service.UserService;
import com.example.lol_likelion.auth.utils.AuthenticationFacade;
import com.example.lol_likelion.user.dto.UserBadgeDto;
import com.example.lol_likelion.user.dto.UserProfileDto;
import com.example.lol_likelion.user.entity.UserBadge;
import com.example.lol_likelion.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.example.lol_likelion.user.service.BadgeService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("users")
@RequiredArgsConstructor
public class UserPageController {
    private final ApiService apiService;
    private final UserService userService;
    private final FollowService followService;
    private final BadgeService badgeService;
    private final AuthenticationFacade authenticationFacade;


    // 소환사 닉네임과 태그로 검색하기
    @PostMapping("/search")
    public String searchSummoner(@RequestParam("gameName") String gameName, @RequestParam("tagLine") String tagLine) {
        // 라이엇 API를 호출하여 소환사 존재 여부 검증
        boolean exists = userService.riotApiCheckGameName(gameName, tagLine);

        if (!exists) {
            // 소환사가 존재하지 않을 경우, fail 페이지로
            return "search-fail";
        }

        // URL 인코딩 수행
        String encodedGameName = URLEncoder.encode(gameName, StandardCharsets.UTF_8);
        String encodedTagLine = URLEncoder.encode(tagLine, StandardCharsets.UTF_8);

        // 인코딩된 값을 사용하여 리디렉션 URL 구성
        return "redirect:/users/" + encodedGameName + "/" + encodedTagLine;
    }

    //유저 페이지로 이동
    @GetMapping("/{gameName}/{tagLine}")
    public String userPageForm(
            @PathVariable String gameName,
            @PathVariable String tagLine,
            Model model) throws Exception {

        String decodedGameName = URLDecoder.decode(gameName, StandardCharsets.UTF_8);
        String decodedTagLine = URLDecoder.decode(tagLine, StandardCharsets.UTF_8);

        //로그인 된 유저인지 확인하기
        Authentication authentication = authenticationFacade.getAuth();
        boolean isAuthenticated = authentication != null &&
                !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);

//======================================Follow 수정=====================================================

        if (isAuthenticated && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userName = userDetails.getUsername();
            log.info("사용자 이름 : {}", userName);

            UserEntity userEntity = userService.getUserByUsername(userName);
            if (userEntity != null) {
                Long userId = userEntity.getId();
                log.info("사용자 id : {}", userId);

                // 로그인한 사용자에 대한 추가 처리...
                UserEntity pageUserEntity = userService.findByGameNameAndTagLine(decodedGameName, decodedTagLine);
                model.addAttribute("pageUser", pageUserEntity); // 페이지 유저 정보 모델에 추가

                if (pageUserEntity != null) {
                    Long pageUserId = pageUserEntity.getId();
                    UserProfileDto dto = followService.userProfile(pageUserId, userId);
                    model.addAttribute("dto", dto); // 팔로우 상태 및 기타 정보 모델에 추가
                }
            }
        } else {
            // 인증되지 않았거나 UserDetails 타입이 아닌 경우의 처리
           log.info("익명 사용자 접근 또는 UserDetails 타입이 아님.");
        }

        //================= match 정보 가져오는 부분 ===============

        //puuid 불러오기
        PuuidDto puuidDto = apiService.callRiotApiPuuid(gameName, tagLine);

        //summonerId 불러오기
        SummonerDto summonerDto = apiService.callRiotApiSummonerId(puuidDto);

        //matchId 가져오기
        MatchIdDto matchIdDto = apiService.callRiotApiMatchId(puuidDto);

        //LeagueEntryDto 불러오는 방법
        LeagueEntryDTO leagueEntryDTO = apiService.getSummonerLeagueEntry(summonerDto);

        //최근 10게임 matchIdList
        List<String> matchIdList = matchIdDto.getMatchIdList();
        //최근 10게임 matchDto를 담을 List
        List<MatchDto> matchDtoList = new ArrayList<>();

        //매 경기 내 정보만(participantDto 중 내 puuid와 같은 내용) 담을 List
        List<MatchDto.InfoDto.ParticipantDto> participantDtoList = new ArrayList<>();
        //matchList의 크기 만큼 반복
        for (String matchId : matchIdList) {
            //한 개의 matchI로 인게임 match 정보 가져오기
            MatchDto matchDto = apiService.callRiotApiMatch(matchId);
            //i번 째 matchDto를 matchDtoList에 담음
            matchDtoList.add(matchDto);
            //i번째 경기 중 내 정보를 담음
            MatchDto.InfoDto.ParticipantDto participantDto = apiService.myInfoFromParticipants(matchDto, puuidDto);
            participantDtoList.add(participantDto);

            System.out.println(matchDto.getInfo().getGameEndTimestamp());
        }

        //model에 넣어서 화면으로 보낸다.
        model.addAttribute("participantDtoList", participantDtoList);
        model.addAttribute("matchDtoList", matchDtoList);
        model.addAttribute("leagueEntryDto", leagueEntryDTO);


        //===========================뱃지 보내기==============================

        //페이지 유저 가져오기
        UserEntity pageUserEntity = userService.findByGameNameAndTagLine(decodedGameName, decodedTagLine);

        if (pageUserEntity != null) {
            // 유저가 DB에 존재하면 뱃지 관련 처리
            List<UserBadgeDto> representBadgeList = badgeService.readAllRepresentBadge(pageUserEntity);
            model.addAttribute("representBadgeList", representBadgeList);

            UserBadge trustBadge = badgeService.readTrustBadge(pageUserEntity);
            model.addAttribute("trustBadge", trustBadge);
        } else {
            // 유저가 DB에 존재하지 않을 경우, 뱃지를 보여주지 않음
            model.addAttribute("representBadgeList", null);
            model.addAttribute("trustBadge", null);
        }

        return "user-page";
    }

    //userPage에서 업데이트 버튼 누르기
    @PostMapping("/{gameName}/{tagLine}")
    public String userPage(@PathVariable String gameName,
                           @PathVariable String tagLine) throws IOException {

        badgeService.userPageUpdate(gameName, tagLine);

        return "redirect:/users/{gameName}/{tagLine}";
    }

    @GetMapping("/represent-badge")
    public String getBadgeList(Model model) {

        //로그인 된 유저인지 확인하기
       Authentication authentication = authenticationFacade.getAuth();
        boolean isAuthenticated = authentication != null &&
                !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);

        //유저 가져오기
        UserEntity user = userService.getUserByUsername(authentication.getName());
        model.addAttribute("user", user);


        List<UserBadgeDto> representBadgeList = badgeService.readAllRepresentBadge(user);
        List<UserBadgeDto> allBadgeExceptTrust = badgeService.readAllBadgeExceptTrust(user);
        model.addAttribute("representBadgeList", representBadgeList);
        model.addAttribute("allBadgeExceptTrust", allBadgeExceptTrust);

        return "select-badge";
    }

    @PostMapping("/represent-badge")
    public String selectRepresentBadge(
            @RequestParam("selectedBadgeId1") Long selectedBadgeId1,
            @RequestParam("selectedBadgeId2") Long selectedBadgeId2,
            Authentication authentication) {
        UserEntity user = userService.getUserByUsername(authentication.getName());

        // 대표 뱃지 설정
        if (selectedBadgeId1 != null && selectedBadgeId2 != null) {
            badgeService.setRepresentBadge(user, selectedBadgeId1, selectedBadgeId2);
        }

        return "redirect:/my-page";
    }

    @PostMapping("/follow/{userPageId}")
    public String follow(@PathVariable Long userPageId, Authentication authentication) throws Exception {
        // 로그인한 사람 ID
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userName = userDetails.getUsername();
        log.info("사용자 이름 : {}", userName);

        UserEntity userEntity = userService.getUserByUsername(userName);
        Long followerId = userEntity.getId();
        log.info("사용자 id : {}" ,followerId);
  
        //===============================================================
  
        UserEntity userPage = userService.findUserById(userPageId);
        String gameName = userPage.getGameName();
        String tagLine = userPage.getTagLine();
        Long followingId = userPageId;

        System.out.println("팔로우할 id : " + followingId);

        followService.follow(followingId,followerId);

        // URL 인코딩 수행
        String encodedGameName = URLEncoder.encode(gameName, StandardCharsets.UTF_8);
        String encodedTagLine = URLEncoder.encode(tagLine, StandardCharsets.UTF_8);

        // 인코딩된 값을 사용하여 리디렉션 URL 구성
        return "redirect:/users/" + encodedGameName + "/" + encodedTagLine;

    }

    @DeleteMapping("/unfollow/{userPageId}")
    public String unfollow(@PathVariable Long userPageId, Authentication authentication) throws Exception {
        // 로그인한 사람 ID
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userName = userDetails.getUsername();
        log.info("사용자 이름 : {}", userName);

        UserEntity userEntity = userService.getUserByUsername(userName);
        Long followerId = userEntity.getId();
        log.info("사용자 id : {}" ,followerId);

        //===============================================================
      
        UserEntity userPage = userService.findUserById(userPageId);
        String gameName = userPage.getGameName();
        String tagLine = userPage.getTagLine();
      //  Long followingId = userPageId;

        followService.unfollow(userPageId, followerId);

        // URL 인코딩 수행
        String encodedGameName = URLEncoder.encode(gameName, StandardCharsets.UTF_8);
        String encodedTagLine = URLEncoder.encode(tagLine, StandardCharsets.UTF_8);

        // 인코딩된 값을 사용하여 리디렉션 URL 구성
        return "redirect:/users/" + encodedGameName + "/" + encodedTagLine;

    }


}
