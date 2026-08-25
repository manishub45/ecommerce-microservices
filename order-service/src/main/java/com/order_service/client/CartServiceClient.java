package com.order_service.client;

import com.order_service.dto.CartResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-service")
public interface CartServiceClient {

        @GetMapping("/api/v1/cart/{uuid}")
        CartResponseDto getCart(@PathVariable("uuid") String uuid);
    }


