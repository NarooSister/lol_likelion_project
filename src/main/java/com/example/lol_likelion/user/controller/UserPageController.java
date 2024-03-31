package com.example.lol_likelion.user.controller;

import com.example.lol_likelion.api.ApiService;
import com.example.lol_likelion.api.dto.MatchIdDto;
import com.example.lol_likelion.api.dto.PuuidDto;
import com.example.lol_likelion.api.dto.SummonerDto;
import com.example.lol_likelion.api.dto.matchdata.MatchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("users")
@RequiredArgsConstructor
public class UserPageController {
    private final ApiService apiService;


    //유저 페이지로 이동
    @GetMapping("/{gameName}/{tagLine}")
    public String userPageForm(
            @PathVariable String gameName,
            @PathVariable String tagLine,
            Model model){

        //puuid 불러오기
        PuuidDto puuidDto = apiService.callRiotApiPuuid(gameName, tagLine);
        String puuid = puuidDto.getPuuid();

        //summonerId 불러오기
        SummonerDto summonerDto = apiService.callRiotApiSummonerId(puuidDto);
        String summonerId = summonerDto.getId();

        //matchId 가져오기
        MatchIdDto matchIdDto = apiService.callRiotApiMatchId(puuidDto);

        //tier 불러오는 방법
        String tier = apiService.getSummonerTierName(summonerDto);


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
        }



        //model에 넣어서 화면으로 보낸다.
        model.addAttribute("participantDtoList", participantDtoList);
        model.addAttribute("matchDtoList", matchDtoList);
        model.addAttribute("championList", championList);


        return "user-page";
    }

    //userPage에서 업데이트 버튼 누르기
    @PostMapping("/{gameName}/{tagLine}")
    public String userPage( @PathVariable String gameName,
                            @PathVariable String tagLine,
                            Model model){

        return "redirect:/user-page";
    }

}
