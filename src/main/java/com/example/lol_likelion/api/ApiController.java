package com.example.lol_likelion.api;

import com.example.lol_likelion.api.dto.MatchIdDto;
import com.example.lol_likelion.api.dto.PuuidDto;
import com.example.lol_likelion.api.dto.SummonerDto;
import com.example.lol_likelion.api.dto.matchdata.MatchDto;
import com.example.lol_likelion.auth.service.UserService;
import com.example.lol_likelion.user.service.BadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")

public class ApiController {

    private final ApiService service;
    private final UserService userService;
    private final BadgeService badgeService;


    @GetMapping("/")
    public PuuidDto callRiotApiPuuid(
            @RequestParam("gameName") String gameName,
            @RequestParam("tagLine") String tagLine) {

        return service.callRiotApiPuuid(gameName, tagLine);
    }

    //Api 테스트 예시입니다.
    @GetMapping("/users1")
    public String callRiotApi(
            @RequestParam("gameName") String gameName,
            @RequestParam("tagLine") String tagLine,
            Model model
    ) {
        //puuid 불러오기
        PuuidDto puuidDto = service.callRiotApiPuuid(gameName, tagLine);
        String puuid = puuidDto.getPuuid();

        //summonerId 불러오기
        SummonerDto summonerDto = service.callRiotApiSummonerId(puuidDto);
        String summonerId = summonerDto.getId();

        //matchId 가져오기
        MatchIdDto matchIdDto = service.callRiotApiMatchId(puuidDto);

        //tier 불러오는 방법
        String tier = service.getSummonerTierName(summonerDto);


        //================= match 정보 가져오는 부분 ===============
        //보여줘야 하는 만큼 넣기

        //최근 5게임 matchIdList
        List<String> matchIdList = matchIdDto.getMatchIdList();
        //최근 5게임 matchDto를 담을 List
        List<MatchDto> matchDtoList = new ArrayList<>();
        //최근 5게임 최근 플레이한 챔피언을 담을 List
        List<String> championList = new ArrayList<>();
        //matchList의 크기 만큼 반복(ex 5)
        for (int i = 0; i < matchIdList.size(); i++) {
            //한 개의 matchId 가져오기
            String matchId = matchIdList.get(i);
            //가져온 matchId로 인게임 match 정보 가져오기
            MatchDto matchDto = service.callRiotApiMatch(matchId);
            //i번 째 matchDto를 matchDtoList에 담음
            matchDtoList.add(matchDto);
            //최근 ?게임동안 플레이 한 챔피언 가져오기
            // i번 째 matchDto에서 championList에 담음
            championList.add(service.recentPlayChampion(matchDto, puuidDto));
        }

        //model에 넣어서 화면으로 보낸다.
        model.addAttribute("matchDtoList", matchDtoList);
        model.addAttribute("championList", championList);

        return "user-page";
    }

    @GetMapping("/users11")
    public void getMatchInfo(
            @RequestParam("gameName") String gameName,
            @RequestParam("tagLine") String tagLine) {
        System.out.println("끝나기 전");
       badgeService.userPageUpdate(gameName,tagLine);
        System.out.println("끝남");
    }
}