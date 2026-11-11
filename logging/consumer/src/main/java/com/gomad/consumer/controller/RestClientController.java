package com.gomad.consumer.controller;

import com.gomad.consumer.rest_client.ProviderRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/rest-client")
@RequiredArgsConstructor
public class RestClientController {

    private final ProviderRestClient providerRestClient;

    @GetMapping
    public String getInstanceInfo(){
        return providerRestClient.getInstanceInfo();
    }

    @GetMapping("/all")
    public String getInstanceInfoAllHere(){
        RestClient restClient = RestClient.create();
        return restClient.get().uri("http://localhost:8081/instance-info")
                .retrieve()
                .body(String.class);
    }
}
