package com.order_service.config;

import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

    @Bean
    public Request.Options feignRequestOptions() {
        return new Request.Options(
                2000, TimeUnit.MILLISECONDS,
                3000, TimeUnit.MILLISECONDS,
                true
        );
    }

    @Bean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }
}