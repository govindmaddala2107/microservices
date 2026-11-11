package com.gomad.consumer.http_interface;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class HttpInterfaceConfig {

    private static final String URL = "http://localhost:8081";

//    Using same loadbalanced webclient here, else you can create your own.
    private final WebClient webClient;

//    @Bean
//    @Qualifier
    public ProviderHttpInterface webClientHttpInterfaceNormal() {
        // or WebClient...
        WebClient webClient = WebClient.create(URL);
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        ProviderHttpInterface service = factory.createClient(ProviderHttpInterface.class);
        return service;
    }

    @Bean
//    @Qualifier
    public ProviderHttpInterface webClientHttpInterfaceLoadBalanced() {
        // or WebClient...
//        WebClient webClient = WebClient.create(URL);
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        ProviderHttpInterface service = factory.createClient(ProviderHttpInterface.class);
        return service;
    }

//    @Bean
    public ProviderHttpInterface RestClientHttpInterface() {
        RestClient restClient = RestClient.create(URL);
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        ProviderHttpInterface service = factory.createClient(ProviderHttpInterface.class);
        return service;
    }
}
