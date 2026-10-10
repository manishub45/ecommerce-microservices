package com.order_service.config;

import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class PaymentFeignConfig {

    @Bean
    public Request.Options paymentFeignRequestOptions() {
        return new Request.Options(
                3000, TimeUnit.MILLISECONDS,   // connect timeout
                8000, TimeUnit.MILLISECONDS,   // read timeout — Payment ko zyada time
                true
        );
    }

    @Bean
    public Retryer paymentFeignRetryer() {
        return Retryer.NEVER_RETRY;
    }
}