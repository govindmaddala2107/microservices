package com.gomad.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

@Configuration
public class RoutingConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter(){
        return new RedisRateLimiter(10, 20, 1);
    }

    @Bean
    public KeyResolver hostNameKeyResolver(){
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("consumer",
                        r -> r
                                .path("/rest-client/**", "/api/feign/**", "/api/web-client/**", "/api/http-interface/**")
                                .filters(f ->
//                                    f.circuitBreaker(config -> config.setName("eComGatewayService"))
//                                    f.retry(retryConfig -> retryConfig.setRetries(10).setMethods(HttpMethod.GET))
                                    f.rewritePath("/rest-client(?<segment>/?.*)", "/api/rest-client${segment}")
                                            .requestRateLimiter(config -> config
                                                    .setRateLimiter(redisRateLimiter())
                                                    .setKeyResolver(hostNameKeyResolver())
                                            )
                                )
                                .uri("lb://CONSUMER"))
                .route("producer", r -> r.path("/instance-info/**")
                        .filters(f -> f.circuitBreaker(config ->
                                config.setName("eComGatewayService").setFallbackUri("forward:/fallback/products")
                        ))
                        .uri("lb://PRODUCER"))
                .route("eureka", r -> r
                        .path("/eureka/main")
                        .filters(f -> f.rewritePath("/eureka/main", "/"))
                        .uri("http://localhost:8761"))
                .route("eureka-server-static", r -> r.path("/eureka/**").uri("http://localhost:8761"))
                .build();
    }
}
