package com.gomad.consumer.controller;

import com.gomad.consumer.http_interface.ProviderHttpInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/http-interface")
public class HttpInterfaceController {

    private final ProviderHttpInterface providerHttpInterface;
    @GetMapping
    public String getInstanceInfo(){
        return providerHttpInterface.getInstanceInfo();
    }
}
