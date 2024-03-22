package com.example.lol_likelion.api;
import com.example.lol_likelion.api.dto.matchdata.MatchDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.lol_likelion.api.dto.LeagueEntryDTO;
import com.example.lol_likelion.api.dto.MatchIdDto;
import com.example.lol_likelion.api.dto.PuuidDto;
import com.example.lol_likelion.api.dto.SummonerDto;
import com.example.lol_likelion.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ApiService {

    @Value("${riot.api.key}")
    private String apiKey;

    private final RestClient authRestClient;

    public ApiService(UserRepository userRepository) {

        this.authRestClient = RestClient
                .builder()
                .build();
    }

    //        String response = authRestClient.get()
//                .uri(url)
//                .retrieve()
//                .body(String.class);
//
//        System.out.println(response);

    //puuid 가져오기(test 완료)
    public PuuidDto callRiotApiPuuid(String gameName, String tagLine) {
        String url = UriComponentsBuilder.fromUriString("https://asia.api.riotgames.com/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}")
                .queryParam("api_key", this.apiKey)
                .buildAndExpand(gameName, tagLine)
                .toUriString();

        return authRestClient.get()
                .uri(url)
                .retrieve()
                .body(PuuidDto.class);
    }

    //puuid로 summonerId 불러오기(test 완료)
    public SummonerDto callRiotApiSummonerId(PuuidDto puuidDto) {
        String url = UriComponentsBuilder.fromUriString("https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/{encryptedPUUID}")
                .queryParam("api_key", this.apiKey)
                .buildAndExpand(puuidDto.getPuuid())
                .toUriString();

        return authRestClient.get()
                .uri(url)
                .retrieve()
                .body(SummonerDto.class);

    }

    //puuid로 matchId 가지고 오기(test 완료)
    public MatchIdDto callRiotApiMatchId(PuuidDto puuidDto) {
        String url = UriComponentsBuilder.fromUriString("https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/{puuid}/ids")
                .queryParam("queue", 420)
                .queryParam("type", "ranked")
                .queryParam("api_key", this.apiKey)
                .buildAndExpand(puuidDto.getPuuid())
                .toUriString();

        List<String> matchIdList = authRestClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});

        MatchIdDto matchIdDto = new MatchIdDto();
        matchIdDto.setMatchIdList(matchIdList);

        return matchIdDto;
    }

    //summonerId로 Tier 가지고 오기 (test 완료)
    //Set으로 받아오기
    public Mono<Set<LeagueEntryDTO>> callRiotApiTier(SummonerDto summonerDto) {
        // summonerId를 받아서 tier로 보내기
        String url = UriComponentsBuilder.fromUriString("https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/{encryptedSummonerId}")
                .queryParam("api_key", this.apiKey)
                .buildAndExpand(summonerDto.getId())
                .toUriString();

        // WebClient를 사용하여 비동기적으로 API 호출
        return WebClient.create()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(LeagueEntryDTO.class) // Flux<LeagueEntryDTO>를 반환하는 API 호출
                .collect(Collectors.toSet()); // Flux를 Set으로 변환하여 반환
    }

    //matchId로 match 정보 가지고 오기(구현중)
    public Mono<MatchDto> callRiotApiMatch(MatchIdDto matchIdDto){
        String url = UriComponentsBuilder.fromUriString("https://asia.api.riotgames.com/lol/match/v5/matches/{matchId}")
                .queryParam("api_key", this.apiKey)
                //TODO: List에서 내 정보만 가지고 오기, 현재 0으로 셋팅
                .buildAndExpand(matchIdDto.getMatchIdList().get(0))
                .toUriString();
        System.out.println(url);

/*        MatchIdDto matchIdDto = new MatchIdDto();
        matchIdDto.setMatchIdList(matchIdList);*/
/*        return authRestClient.get()
                .uri(url)
                .retrieve()
                .body(MatchDto.class);*/

        return WebClient.create()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(MatchDto.class);

    }


}
