package com.example.lol_likelion.auth.config;

import com.example.lol_likelion.auth.dto.ChampionDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Configuration
public class ChampionDataLoader {
    public String findChampionIdByKey(String key) {
        try {
            InputStream jsonInputStream = new ClassPathResource("data/championData.json").getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonInputStream);

            // 모든 챔피언을 순회
            for (JsonNode champion : rootNode) {
                String championKey = champion.path("key").asText();
                if (championKey.equals(key)) {
                    return champion.path("id").asText(); // 일치하는 key 값을 가진 챔피언의 id 반환
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
}