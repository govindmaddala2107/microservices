package com.gomad.consumer.rest_client;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Autowired(required = false)
    private ObservationRegistry observationRegistry;

    @Autowired(required = false)
    private Tracer tracer;

    @Autowired(required = false)
    private Propagator propagator;

    /**
     * Creates a load-balanced builder for requests addressed by Eureka service name.
     */
    @Bean
    @LoadBalanced
    @Scope("prototype")
    public RestClient.Builder loadBalancedRestClientBuilder() {
        RestClient.Builder builder = RestClient.builder();
        if(observationRegistry != null){
            builder.requestInterceptor(createTrackingInterceptor());
        }
        return builder;
    }

    private ClientHttpRequestInterceptor createTrackingInterceptor() {
        return ((request, body, execution) -> {
            if(tracer != null
                && propagator != null
                    && tracer.currentSpan() != null
            ){
                propagator.inject(
                        tracer.currentTraceContext().context(),
                        request.getHeaders(),
                        (carrier, key, value) -> carrier.add(key, value)
                        );
            }
            return execution.execute(request, body);
        });
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