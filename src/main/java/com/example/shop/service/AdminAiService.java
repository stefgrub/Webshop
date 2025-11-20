package com.example.shop.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminAiService {

    private final WebClient.Builder webClientBuilder;

    @Value("${webshop.ai.base-url}")
    private String aiBaseUrl;

    @Value("${webshop.ai.bearer-token}")
    private String aiBearerToken;

    private WebClient client() {
        return webClientBuilder
                .clone()
                .baseUrl(aiBaseUrl)
                .defaultHeader("Authorization", "Bearer " + aiBearerToken)
                .build();
    }

    public String askAssistant(String question, String adminUser) {
        JsonNode resp = client()
                .post()
                .uri("/chat")
                .bodyValue(Map.of(
                        "question", question,
                        "adminUser", adminUser
                ))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (resp == null || resp.get("answer") == null) {
            return "(Keine Antwort vom AI-Service)";
        }
        return resp.get("answer").asText();
    }
}