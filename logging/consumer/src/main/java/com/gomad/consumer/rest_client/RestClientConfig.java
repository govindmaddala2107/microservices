package com.gomad.consumer.rest_client;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    /**
     * Creates a load-balanced builder for requests addressed by Eureka service name.
     */
    @Bean
    @LoadBalanced
    @Scope("prototype")
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    /**
     * Provides the default non-load-balanced builder for framework HTTP clients, including Eureka.
     */
    @Bean
    @Primary
    @Scope("prototype")
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    /**
     * Builds a client that resolves the PRODUCER service through Eureka and the load balancer.
     */
    @Bean
    public RestClient restClient(@LoadBalanced RestClient.Builder builder) {
        return builder.baseUrl("http://PRODUCER").build();
    }
}