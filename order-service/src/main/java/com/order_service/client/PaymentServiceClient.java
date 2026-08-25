package com.order_service.client;



import com.order_service.dto.PaymentRequestDto;
import com.order_service.dto.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/api/v1/payment/create")
    PaymentResponseDto createPayment(@RequestBody PaymentRequestDto requestDto);
}