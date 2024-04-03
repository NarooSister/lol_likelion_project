package com.example.lol_likelion.user.controller;

import com.example.lol_likelion.api.ApiService;
import com.example.lol_likelion.api.dto.LeagueEntryDTO;
import com.example.lol_likelion.api.dto.MatchIdDto;
import com.example.lol_likelion.api.dto.PuuidDto;
import com.example.lol_likelion.api.dto.SummonerDto;
import com.example.lol_likelion.api.dto.matchdata.MatchDto;
import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.auth.service.UserService;
import com.example.lol_likelion.user.dto.UserProfileDto;
import com.example.lol_likelion.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.lol_likelion.user.service.BadgeService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        // 인코딩된 값을 사용하여 리디렉션 URL 구성
        return "redirect:/users/" + encodedGameName + "/" + encodedTagLine;
    }

    //유저 페이지로 이동
    @GetMapping("/{gameName}/{tagLine}")
    public String userPageForm(
            @PathVariable String gameName,
            @PathVariable String tagLine,
            Model model) throws Exception {

        //로그인 된 유저인지 확인하기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null &&
                !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);



        //======================follow 고치려는 노력.....==================================

/*

        // follow 수정
        // view나 service는 하나도 안고쳤습니당!

        // 사용자 1 = 페이지에 들어간 유저, 사용자 2 = 페이지의 주인 유저
        // 1 비인증 2 인증 -> followers,following 보임
        // 1 비인증 2 비인증 -> 암것도 안보임
        // 1 인증 2 인증 -> followers,following,팔로우 차단 버튼 다 보임
        // 1 인증 2 비인증 -> 암것도 안보임

        UserEntity pageOwnerUser = userService.findByGameNameAndTagLine(gameName, tagLine);

        // 사용자 2(페이지 주인)의 인증 상태는 확인 x
        // 사용자 1이 인증된 경우에만 팔로우 관련 정보를 처리.
        // isAuthenticated 는 위에서 가져온 로그인한 유저인지 확인하는 과정
        if (isAuthenticated) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userName = userDetails.getUsername();
            UserEntity loggedInUser = userService.getUserByUsername(userName); // 로그인한 사용자(사용자 1)

            // 사용자 2에 대한 정보가 있는 경우에만 팔로우 관련 정보를 처리.
            //view에서 canFollow, canBlock 등으로 팔로우 버튼 차단 버튼 활성화 가능
            if (pageOwnerUser != null) {
                UserProfileDto userProfileDto = followService.userProfile(pageOwnerUser.getId(), loggedInUser.getId());
                model.addAttribute("userProfile", userProfileDto);
                model.addAttribute("canFollow", true); // 팔로우 버튼 표시
                model.addAttribute("canBlock", true); // 차단 버튼 표시
                //TODO: follow한 유저인 경우 언팔버튼으로 변경....?
                //만약 기능 넣으려면 여기서 follow한 유저인지 확인하는 메소드 하나 추가해서 검증하고 넣기
            } else {
                // 사용자 2가 없는 경우, 팔로우와 차단 버튼을 표시하지 않음.
                model.addAttribute("canFollow", false);
                model.addAttribute("canBlock", false);
            }
        } else {
            // 사용자 1이 비인증인 경우, 팔로우와 차단 버튼을 표시하지 않음.
            model.addAttribute("canFollow", false);
            model.addAttribute("canBlock", false);
        }
*/

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


        return "user-page";
    }

    //userPage에서 업데이트 버튼 누르기
    @PostMapping("/{gameName}/{tagLine}")
    public String userPage( @PathVariable String gameName,
                            @PathVariable String tagLine,
                            Model model){

        badgeService.userPageUpdate(gameName,tagLine);

        return "redirect:/users/{gameName}/{tagLine}";
    }

}
