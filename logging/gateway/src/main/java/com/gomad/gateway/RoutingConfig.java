package com.gomad.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class RoutingConfig {

//    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("consumer",
                        r -> r
                                .path("/rest-client/**", "/api/feign/**", "/api/web-client/**", "/api/http-interface/**")
                                .filters(f -> f.rewritePath("/rest-client(?<segment>/?.*)", "/api/rest-client${segment}"))
                                .uri("lb://CONSUMER"))
                .route("producer", r -> r.path("/instance-info/**").uri("lb://PRODUCER"))
                .route("eureka", r -> r
                        .path("/eureka/main")
                        .filters(f -> f.rewritePath("/eureka/main", "/"))
                        .uri("http://localhost:8761"))
                .route("eureka-server-static", r -> r.path("/eureka/**").uri("http://localhost:8761"))
                .build();
    }
}
