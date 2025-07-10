package com.example.demo.service;
import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LlmService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    private final WebClient webClient = WebClient.builder()
            .baseUrl(apiUrl)
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .defaultHeader("Content-Type", "application/json")
            .build();

    public Mono<String> extractResponse(Map<String, Object> body) {
        var choices = (java.util.List<Map<String, Object>>) body.get("choices");
        if (choices != null && !choices.isEmpty()) {
            var message = (Map<String, Object>) choices.get(0).get("message");
            return Mono.justOrEmpty((String) message.get("content"));
        }
        return Mono.empty();
    }

    public Mono<String> queryLlm(String prompt, boolean stream) {
        var body = Map.of(
                "model", model,
                "stream", false,
                "messages", java.util.List.of(Map.of("role", "user", "content", prompt))
        );

        return webClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(this::extractResponse)
                .timeout(Duration.ofSeconds(30))
                .onErrorResume(e -> Mono.error(new RuntimeException("Error: " + e.getMessage())));
    }

    public Flux<String> queryLlm1(String prompt, boolean stream) {
        if (!stream) return queryLlm(prompt, false).flux();

        var body = Map.of(
                "model", model,
                "stream", true,
                "messages", java.util.List.of(Map.of("role", "user", "content", prompt))
        );

        return webClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class)
                .onErrorResume(e -> Flux.just("Stream Error: " + e.getMessage()));
    }
}