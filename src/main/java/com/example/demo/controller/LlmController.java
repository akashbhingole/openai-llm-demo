package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LlmRequest;
import com.example.demo.dto.LlmResponse;
import com.example.demo.service.LlmService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/llm")
public class LlmController {

    @Autowired
    private LlmService llmService;

    @PostMapping("/query")
    public Mono<LlmResponse> query(@RequestBody LlmRequest request) {
        return llmService.queryLlm(request.getPrompt(), false)
                         .map(LlmResponse::new);
    }

    @PostMapping(value = "/query", produces = MediaType.TEXT_EVENT_STREAM_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, params = "stream=true")
    public Flux<String> stream(@RequestBody LlmRequest request) {
        return llmService.queryLlm1(request.getPrompt(), true);
    }
}
