package com.example.lol_likelion.api;

import com.example.lol_likelion.api.dto.*;
import com.example.lol_likelion.api.dto.matchdata.MatchDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.lol_likelion.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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

    //puuid로 gamaName과 tagLine 가져오기
    public GameNameDto callRiotApiGameNameAndTagLine(String puuid) {
        String url = UriComponentsBuilder.fromUriString("https://asia.api.riotgames.com/riot/account/v1/accounts/by-puuid/{puuid}")
                .queryParam("api_key", this.apiKey)
                .buildAndExpand(puuid)
                .toUriString();

        return authRestClient.get()
                .uri(url)
                .retrieve()
                .body(GameNameDto.class);
    }

    //puuid로 summonerId 불러오기(test 완료)
    //SummonerDto로 profileIconId도 같이 가져오기
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
                .queryParam("count", 5)
                .buildAndExpand(puuidDto.getPuuid())
                .toUriString();

        List<String> matchIdList = authRestClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        MatchIdDto matchIdDto = new MatchIdDto();
        matchIdDto.setMatchIdList(matchIdList);

        return matchIdDto;
    }
    //puuid로 matchId 가지고 오기 -> 시작 시간 정해서 가지고 오기
    //시작 시간 이후의 match 중에 가장 최근 match 부터 가져옴
    public MatchIdDto callRiotApiMatchIdByTime(String puuid, Long startTime) {

        String url = UriComponentsBuilder.fromUriString("https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/{puuid}/ids")
                .queryParam("startTime", startTime)
                .queryParam("queue", 420)
                .queryParam("type", "ranked")
                .queryParam("api_key", this.apiKey)
                .queryParam("count", 5)
                .buildAndExpand(puuid)
                .toUriString();

        List<String> matchIdList = authRestClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        MatchIdDto matchIdDto = new MatchIdDto();
        matchIdDto.setMatchIdList(matchIdList);

        return matchIdDto;
    }

    //summonerId로 Tier 가지고 오기 (test 완료)
    //Tier 정보 Set으로 받아오기
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

    // Tier 가져오기 끝!!!!
    // 소환사의 티어 정보 중에서 특정 정보(예: 티어 이름)를 추출하여 반환하는 메서드
    public String getSummonerTierName(SummonerDto summonerDto) {
        Set<LeagueEntryDTO> tierInfoSet = callRiotApiTier(summonerDto).block(); // Mono의 결과를 동기적으로 가져옴

        if (tierInfoSet != null && !tierInfoSet.isEmpty()) {
            LeagueEntryDTO leagueEntry = tierInfoSet.iterator().next(); // 첫 번째 항목 가져옴
            return leagueEntry.getTier(); // 티어 이름 반환
        } else {
            return "Unranked"; // 티어 정보가 없는 경우 기본값 반환
        }
    }
    // 소환사의 티어 정보 중에서 특정 정보(예: 티어 이름)를 추출하여 반환하는 메서드
    public LeagueEntryDTO getSummonerLeagueEntry(SummonerDto summonerDto) {
        Set<LeagueEntryDTO> LeagueInfoSet = callRiotApiTier(summonerDto).block(); // Mono의 결과를 동기적으로 가져옴

        if (LeagueInfoSet != null && !LeagueInfoSet.isEmpty()) {
            return LeagueInfoSet.iterator().next(); // LeagueEntryDto 반환
        } else {
            return null;
        }
    }

    //matchId로 match 정보 1개 가지고 오기 (test 완료)
    public MatchDto callRiotApiMatch(String matchId) {
        String url = UriComponentsBuilder.fromUriString("https://asia.api.riotgames.com/lol/match/v5/matches/{matchId}")
                .queryParam("api_key", this.apiKey)
                .buildAndExpand(matchId)
                .toUriString();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            String response = authRestClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);

            // JSON 문자열을 MatchDto 객체로 변환
            MatchDto matchDto = objectMapper.readValue(response, MatchDto.class);

            return matchDto;
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
            return null; // 예외 발생 시 null 반환
        }
    }

    //partivipant에서 내 정보만 가져오기
    public MatchDto.InfoDto.ParticipantDto myInfoFromParticipants(MatchDto matchDto, PuuidDto puuidDto) {
        //플레이어 10명 정보 가져오기
        List<String> participantsPuuid = matchDto.getMetadata().getParticipants();
        //나랑 puuid가 같은 participant의 순서
        String puuid = puuidDto.getPuuid(); //내 puuid
        int index = participantsPuuid.indexOf(puuid);
        //infoDto 안에 participantDto
        List<MatchDto.InfoDto.ParticipantDto> participants = matchDto.getInfo().getParticipants();
        return participants.get(index);
    }

    //최근 ?게임동안 플레이 한 챔피언
    public String recentPlayChampion(MatchDto matchDto, PuuidDto puuidDto) {
        //플레이어 10명 정보 가져오기
        List<String> participantsPuuid = matchDto.getMetadata().getParticipants();
        //나랑 puuid가 같은 participant의 순서
        String puuid = puuidDto.getPuuid(); //내 puuid
        int index = participantsPuuid.indexOf(puuid);
        //infoDto 안에 participantDto
        List<MatchDto.InfoDto.ParticipantDto> participants = matchDto.getInfo().getParticipants();
        return participants.get(index).getChampionName();
    }




    //TEST로 정보 가져와보기(확인하기 위함)
    public void processMatchInformation(MatchDto matchDto) {
        // 매치 시간 정보 가져오기
        long gameDuration = matchDto.getInfo().getGameDuration();
        System.out.println("게임 시간: " + gameDuration + "초");


        // 플레이어 정보 가져오기
        List<MatchDto.InfoDto.ParticipantDto> participants = matchDto.getInfo().getParticipants();
        for (MatchDto.InfoDto.ParticipantDto participant : participants) {
            System.out.println("플레이어: " + participant.getPuuid() + ", 챔피언: " + participant.getChampionName());
        }

        // 필요한 정보를 추출하여 다양한 작업 수행
        // 예를 들어, 승리한 팀의 플레이어 목록을 가져와 특정 작업을 수행할 수 있습니다.
        List<MatchDto.InfoDto.ParticipantDto> winningTeamPlayers = new ArrayList<>();
        for (MatchDto.InfoDto.ParticipantDto participant : participants) {
            if (participant.getTeamId() == 100 && participant.getWin()) {
                winningTeamPlayers.add(participant);
            }
        }
        System.out.println("승리한 팀의 플레이어 목록: " + winningTeamPlayers);
    }


}
