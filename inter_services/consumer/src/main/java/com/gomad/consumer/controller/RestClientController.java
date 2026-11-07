package com.gomad.consumer.controller;

import com.gomad.consumer.rest_template.RestTemplateClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rest-template")
@RequiredArgsConstructor
public class RestTemplateController {

    private final RestTemplateClient restTemplateClient;

    @GetMapping
    public String getInstanceInfo(){
        return restTemplateClient.getInstanceInfo();
    }
}
