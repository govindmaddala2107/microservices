package com.gomad.consumer.controller;

import com.gomad.consumer.web_client.ProviderWebClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/web-client")
public class WebClientController {

    private final ProviderWebClient providerWebClient;

    @GetMapping
    public Mono<String> getInstanceInfo(){
        return providerWebClient.getInstanceInfo();
    }

    @GetMapping("/basic")
    public Mono<String> getInstanceInfoBasic(){
        WebClient webClient = WebClient.create();
        return webClient.get()
                .uri("http://localhost:8081/instance-info")
                .retrieve()
                .bodyToMono(String.class);
    }

}
