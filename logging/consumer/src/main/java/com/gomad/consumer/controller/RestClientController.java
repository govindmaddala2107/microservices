package com.gomad.consumer.controller;

import com.gomad.consumer.rest_client.ProviderRestClient;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/rest-client")
@RequiredArgsConstructor
public class RestClientController {

    private final ProviderRestClient providerRestClient;

    int count = 0;

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

    @GetMapping("/names")
    @RateLimiter(name = "ecomRateLimiter", fallbackMethod = "rateLimiterRetryFallback")
    public ResponseEntity<String> getGreetingMessage(@RequestParam("name") String name){
        if(!name.equalsIgnoreCase("gomad")){
            throw new RuntimeException("Not allowed and tried for " + ++count + " times");
//            return new ResponseEntity<>("Not allowed", HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(String.format("Hello Mr.%s", name), HttpStatus.OK);
    }

    public ResponseEntity<String> rateLimiterRetryFallback(@RequestParam("name") String name, Exception ex){
        return new ResponseEntity<>("Rate limit exceeded please wait.", HttpStatus.OK);
    }

    @GetMapping("/rate-limiter")
    public ResponseEntity<String> getGreetingMessageForRateLimiter(){
        return new ResponseEntity<>(String.format("Hello Mr.%s", "govind Roshini"), HttpStatus.OK);
    }
}
