package com.order_service.controller;

import com.order_service.dto.OrderCancelResponse;
import com.order_service.dto.OrderPlacedResponse;
import com.order_service.dto.PlaceOrderRequest;
import com.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public ResponseEntity<OrderPlacedResponse> placeOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody PlaceOrderRequest request) {

        OrderPlacedResponse response = orderService.placeOrder(userId, idempotencyKey, request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{orderId}/cancel")   // naya
    public ResponseEntity<OrderCancelResponse> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader("X-User-Id") Long userId) {

        OrderCancelResponse response = orderService.cancelOrder(orderId, userId);
        return ResponseEntity.ok(response);
    }
}