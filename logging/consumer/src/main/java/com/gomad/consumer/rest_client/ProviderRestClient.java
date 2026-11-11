package com.gomad.consumer.rest_client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class ProviderRestClient {

    private final RestClient restClient;

    public String getInstanceInfo(){
//        String url = "http://localhost:8081/instance-info";
        return restClient.get().uri("/instance-info")
                .retrieve()
                .body(String.class);
    }
}
