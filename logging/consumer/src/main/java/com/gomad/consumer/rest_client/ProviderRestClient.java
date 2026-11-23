package com.gomad.consumer.rest_client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class ProviderRestClient {

    private final RestClient restClient;
    int attempt = 0;

//    @CircuitBreaker(name = "producerService", fallbackMethod = "getInstanceInfoFallback")
//    @Retry(name = "ecomRetryBreaker", fallbackMethod = "getInstanceInfoFallback")
    public String getInstanceInfo(){
        System.out.println("Tried for " + ++attempt);
//        String url = "http://localhost:8081/instance-info";
        return restClient.get().uri("/instance-info")
                .retrieve()
                .body(String.class);
    }

    public String getInstanceInfoFallback(Throwable throwable){
        return "No instances available for PRODUCER";
    }
}
