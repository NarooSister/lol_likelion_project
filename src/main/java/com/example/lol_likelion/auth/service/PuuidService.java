package com.example.lol_likelion.auth.service;

import com.example.lol_likelion.api.dto.PuuidDto;
import com.example.lol_likelion.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;


@Slf4j
@Service
public class PuuidService {

    @Value("${riot.api.key}")
    private String apiKey;

    private final RestClient authRestClient;

    public PuuidService(UserRepository userRepository)
    {

        this.authRestClient = RestClient
                .builder()
                .baseUrl("https://asia.api.riotgames.com/")
                .build();
    }


    public PuuidDto callRiotApiPuuid(String gameName, String tagLine)
    {
        String url = UriComponentsBuilder.fromUriString("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}")
                .queryParam("api_key", this.apiKey)
                .buildAndExpand(gameName, tagLine)
                .toUriString();

        log.info(url);

//        String response = authRestClient.get()
//                .uri(url)
//                .retrieve()
//                .body(String.class);

        return authRestClient.get()
                .uri(url)
                .retrieve()
                .body(PuuidDto.class);
    }

}
