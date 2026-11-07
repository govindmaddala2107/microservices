package com.gomad.consumer.controller;

import com.gomad.consumer.feign.ProviderFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feign")
@RequiredArgsConstructor
public class FeignController {

    private final ProviderFeignClient providerFeignClient;

    @GetMapping
    public String getInstanceInfo(){
        return providerFeignClient.getInstanceInfo();
    }
}
