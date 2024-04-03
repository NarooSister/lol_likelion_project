package com.example.lol_likelion.user.controller;

import com.example.lol_likelion.api.ApiService;
import com.example.lol_likelion.api.dto.LeagueEntryDTO;
import com.example.lol_likelion.api.dto.MatchIdDto;
import com.example.lol_likelion.api.dto.PuuidDto;
import com.example.lol_likelion.api.dto.SummonerDto;
import com.example.lol_likelion.api.dto.matchdata.MatchDto;
import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.auth.service.UserService;
import com.example.lol_likelion.user.dto.UserBadgeDto;
import com.example.lol_likelion.user.dto.UserProfileDto;
import com.example.lol_likelion.user.entity.Follow;
import com.example.lol_likelion.user.entity.Badge;
import com.example.lol_likelion.user.entity.UserBadge;
import com.example.lol_likelion.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.lol_likelion.user.service.BadgeService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("users")
@RequiredArgsConstructor
public class UserPageController {
    private final ApiService apiService;
    private final UserService userService;
    private final FollowService followService;
    private final BadgeService badgeService;


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
        System.out.println("encodedGameName:" + encodedGameName);
        System.out.println("encodedTagLine:" + encodedTagLine);

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null &&
                !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);


        // ============================follow============================
      
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userName = userDetails.getUsername();
        System.out.println("사용자 이름 : " + userName);

        UserEntity userEntity = userService.getUserByUsername(userName);
        Long userId = userEntity.getId();
        System.out.println("사용자 id : " + userId);

        // 검색해서 받아올 때 띄어쓰기가 있으면 A+B+C 이렇게 받아와지는데
        // DB 에는 A B C 이렇게 되어있기 때문에 gameName 을 찾지 못해서 null 로 계속 보내졌던 거다 !!!
        // 그래서 replace 해줘서 다시 받아와야 한다 !!
        String changeGameName = gameName.replace('+', ' ');

        UserEntity userEntity2 = userService.findByGameNameAndTagLine(changeGameName, tagLine);

        if (userEntity2 != null) {
            Long pageUserId = userEntity2.getId();
            System.out.println("gameName/tagLine : " + pageUserId);

            UserProfileDto dto = followService.userProfile(pageUserId, userId);
            model.addAttribute("dto", dto);

            System.out.println(dto);
        } else {
            System.out.println("유저가 아님");
        }
      
        // ==============================================================

        //puuid 불러오기
        PuuidDto puuidDto = apiService.callRiotApiPuuid(gameName, tagLine);
        String puuid = puuidDto.getPuuid();

        //summonerId 불러오기
        SummonerDto summonerDto = apiService.callRiotApiSummonerId(puuidDto);
        String summonerId = summonerDto.getId();

        //matchId 가져오기
        MatchIdDto matchIdDto = apiService.callRiotApiMatchId(puuidDto);

        //LeagueEntryDto 불러오는 방법
        LeagueEntryDTO leagueEntryDTO = apiService.getSummonerLeagueEntry(summonerDto);


        //================= match 정보 가져오는 부분 ===============
        //보여줘야 하는 만큼 넣기

        //최근 5게임 matchIdList
        List<String> matchIdList = matchIdDto.getMatchIdList();
        //최근 5게임 matchDto를 담을 List
        List<MatchDto> matchDtoList = new ArrayList<>();
        //최근 5게임 최근 플레이한 챔피언을 담을 List
        List<String> championList = new ArrayList<>();
        //매 경기 내 정보만(participantDto 중 내 puuid와 같은 내용) 담을 List
        List<MatchDto.InfoDto.ParticipantDto> participantDtoList = new ArrayList<>();
        //matchList의 크기 만큼 반복(ex 5)
        for (int i = 0; i < 2/*matchIdList.size()*/; i++) {
            //한 개의 matchId 가져오기
            String matchId = matchIdList.get(i);
            //가져온 matchId로 인게임 match 정보 가져오기
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
        model.addAttribute("championList", championList);
        model.addAttribute("leagueEntryDto", leagueEntryDTO);


        //===========================뱃지 보내기==============================
        //페이지 유저 가져오기
        UserEntity pageUserEntity = userService.findByGameNameAndTagLine(decodedGameName, decodedTagLine);

        //대표 뱃지 목록 보내기
        List<UserBadgeDto> representBadgeList = badgeService.readAllRepresentBadge(pageUserEntity);
        model.addAttribute("representBadgeList", representBadgeList);

        //신뢰 뱃지 보내기
        UserBadge trustBadge = badgeService.readTrustBadge(pageUserEntity);
        // trustBadge가 존재하지 않는 경우는 null 보냄
        model.addAttribute("trustBadge", trustBadge);

        return "user-page";
    }

    //userPage에서 업데이트 버튼 누르기
    @PostMapping("/{gameName}/{tagLine}")
    public String userPage(@PathVariable String gameName,
                           @PathVariable String tagLine,
                           Model model) {

        badgeService.userPageUpdate(gameName, tagLine);

        return "redirect:/users/{gameName}/{tagLine}";
    }

    @GetMapping("/represent-badge")
    public String getBadgeList(Model model, Authentication authentication) {
        //로그인 된 유저인지 확인하기
        authentication = SecurityContextHolder.getContext().getAuthentication();
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
    public String follow(@PathVariable Long userPageId, Model model,  Authentication authentication) throws Exception {
        // 로그인한 사람 ID
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userName = userDetails.getUsername();
        System.out.println("사용자 이름 : " + userName);

        UserEntity userEntity = userService.getUserByUsername(userName);
        Long followerId = userEntity.getId();
        System.out.println("사용자 id : " + followerId);
  
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
    public String unfollow(@PathVariable Long userPageId, Model model,  Authentication authentication) throws Exception {
        // 로그인한 사람 ID
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userName = userDetails.getUsername();
        System.out.println("사용자 이름 : " + userName);

        UserEntity userEntity = userService.getUserByUsername(userName);
        Long followerId = userEntity.getId();
        System.out.println("사용자 id : " + followerId);

        //===============================================================
      
        UserEntity userPage = userService.findUserById(userPageId);
        String gameName = userPage.getGameName();
        String tagLine = userPage.getTagLine();
        Long followingId = userPageId;
        // URL 인코딩 수행
        String encodedGameName = URLEncoder.encode(gameName, StandardCharsets.UTF_8);
        String encodedTagLine = URLEncoder.encode(tagLine, StandardCharsets.UTF_8);

        followService.unfollow(userPageId, followerId);
        // 인코딩된 값을 사용하여 리디렉션 URL 구성
        return "redirect:/users/" + encodedGameName + "/" + encodedTagLine;

    }


}
