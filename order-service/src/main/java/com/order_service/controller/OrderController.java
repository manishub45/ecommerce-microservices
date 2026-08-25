package com.order_service.controller;

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
    public ResponseEntity<OrderPlacedResponse> placeOrder(@RequestBody PlaceOrderRequest request) {
        OrderPlacedResponse response = orderService.placeOrder(request);
        return ResponseEntity.ok(response);
    }
}