package com.order_service.client;

import com.order_service.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @GetMapping("/auth/v1/user/{id}")
    UserDto getUserById(@PathVariable("id") Long id);
}
