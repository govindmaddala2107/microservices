package com.gomad.producer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstanceController {

    @Value("${server.port}")
    private String port;

    private final String instanceId = java.util.UUID.randomUUID().toString();

    @GetMapping("/instance-info")
    public String getInstanceInfo(){
        System.out.printf("Request received at instance running on port: %s\n", port);
        return String.format("Instance served by PORT: %s. Instance ID: %s", port, instanceId);
    }
}
