package com.gomad.consumer.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "Provider-Service", url = "http://localhost:8081")
public interface ProviderFeignClient {

    @GetMapping("/instance-info")
    String getInstanceInfo();
}
