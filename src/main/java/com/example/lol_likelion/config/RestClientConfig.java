package com.example.lol_likelion.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

   // private final tokenService;
   @Bean
    public RestClient lolClient()
    {
        return RestClient.builder()
                .baseUrl("https://asia.api.riotgames.com")
                .build();
    }

}
