package com.example.lol_likelion.api;

import com.example.lol_likelion.api.dto.PuuidDto;
import com.example.lol_likelion.auth.service.PuuidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SummonerController {

    private final PuuidService service;

    @GetMapping("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}")
    public PuuidDto callRiotApiPuuid(
            @PathVariable("gameName") String gameName,
            @PathVariable("tagLine") String tagLine)
    {
        return service.callRiotApiPuuid(gameName, tagLine);
    }

    @GetMapping("champion")
    public void champion()
    {
    //    return service.callRiotAPISummonerName(username);
    }

}
