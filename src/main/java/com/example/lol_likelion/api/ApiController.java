package com.example.lol_likelion.api;

import com.example.lol_likelion.api.dto.LeagueEntryDTO;
import com.example.lol_likelion.api.dto.MatchIdDto;
import com.example.lol_likelion.api.dto.PuuidDto;
import com.example.lol_likelion.api.dto.SummonerDto;
import com.example.lol_likelion.api.dto.matchdata.MatchDto;
import com.example.lol_likelion.api.dto.matchdata.ParticipantDto1;
import com.example.lol_likelion.auth.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ApiController {

    private final ApiService service;

    @GetMapping("/")
    public PuuidDto callRiotApiPuuid(
            @RequestParam("gameName") String gameName,
            @RequestParam("tagLine") String tagLine) {

        return service.callRiotApiPuuid(gameName, tagLine);
    }

    //Api 테스트 예시입니다.
    @GetMapping("users1")
    public String callRiotApi(
            @RequestParam("gameName") String gameName,
            @RequestParam("tagLine") String tagLine
    ) {
        //puuid 불러오기
        PuuidDto puuidDto = service.callRiotApiPuuid(gameName, tagLine);
        String puuid = puuidDto.getPuuid();

        //summonerId 불러오기
        SummonerDto summonerDto = service.callRiotApiSummonerId(puuidDto);
        String summonerId = summonerDto.getId();

        //tier 불러오는 방법
        UserInfoDto userInfoDto = new UserInfoDto();
        service.callRiotApiTier(summonerDto)
                .subscribe(leagueEntrySet -> {
                    for (LeagueEntryDTO entry : leagueEntrySet) {
                        String tier = entry.getTier();
                        // tier 정보를 사용하여 필요한 작업을 수행
                        System.out.println("Tier: " + tier);
                        //userInfoDto에 저장
                        userInfoDto.setTier(tier);
                    }
                });

        //matchId 가져오기
        MatchIdDto matchIdDto = service.callRiotApiMatchId(puuidDto);
        List<String> matchId = matchIdDto.getMatchIdList();



        //match 가져오기
        service.callRiotApiMatch(matchIdDto);
        MatchDto matchDto = new MatchDto();

        System.out.println("### matchDto : " + matchDto);
/*        MetadataDto metadataDto = new MetadataDto();
        InfoDto infoDto = new InfoDto();
        ParticipantDto1 participantDto = new ParticipantDto1();
*/
        MatchDto.InfoDto infoDto = new MatchDto.InfoDto();


        System.out.println(infoDto.getGameStartTimestamp());

        return "Api Test done";
    }
    @GetMapping("users11")
    public Mono<String> getMatchInfo(
            @RequestParam("gameName") String gameName,
            @RequestParam("tagLine") String tagLine) {

        // puuid 불러오기
        Mono<PuuidDto> puuidDtoMono = Mono.just(service.callRiotApiPuuid(gameName, tagLine));

        // summonerId 불러오기 및 match 정보 가져오기를 연쇄적으로 수행
        return puuidDtoMono.flatMap(puuidDto -> {
            MatchIdDto matchIdDto = service.callRiotApiMatchId(puuidDto);
            return service.callRiotApiMatch(matchIdDto)
                    .map(matchDto -> {
                        /*// MatchDto 내용 확인을 위한 로그 추가
                        log.info("Match 정보 가져옴: Game Duration: {}", matchDto.getInfo().getGameDuration());

                        // 참가자 정보 로그 출력
                        if (!matchDto.getInfo().getParticipants().isEmpty()) {
                            MatchDto.InfoDto.ParticipantDto participant = matchDto.getInfo().getParticipants().get(0); // 예시로 첫 번째 참가자만 출력
                            log.info("First participant - Kills: {}, Deaths: {}, Assists: {}",
                                    participant.getKills(), participant.getDeaths(), participant.getAssists());
                        }*/
                        return "Match 정보 처리 완료";
                    });
        }).doOnError(error -> {
            // 에러 처리 로직
            log.error("Match 정보 처리 중 에러 발생", error);
        });
    }




}