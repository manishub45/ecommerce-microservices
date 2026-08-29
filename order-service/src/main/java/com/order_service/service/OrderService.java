package com.order_service.service;

import com.order_service.client.AuthServiceClient;
import com.order_service.client.CartServiceClient;
import com.order_service.client.PaymentServiceClient;
import com.order_service.dto.*;
import com.order_service.entity.Order;
import com.order_service.entity.OrderItem;
import com.order_service.entity.OrderStatus;
import com.order_service.kafka.OrderEventProducer;
import com.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartServiceClient cartServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final OrderEventProducer orderEventProducer;
    private final AuthServiceClient authServiceClient;

    public OrderService(OrderRepository orderRepository, CartServiceClient cartServiceClient,
                        PaymentServiceClient paymentServiceClient, OrderEventProducer orderEventProducer,
                        AuthServiceClient authServiceClient) {
        this.orderRepository = orderRepository;
        this.cartServiceClient = cartServiceClient;
        this.paymentServiceClient = paymentServiceClient;
        this.orderEventProducer = orderEventProducer;
        this.authServiceClient = authServiceClient;
    }

    public OrderPlacedResponse placeOrder(PlaceOrderRequest request) {

        CartResponseDto cart = cartServiceClient.getCart(request.getCartUuid());

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.PLACED);
        order.setOrderDate(LocalDateTime.now());
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItemDto cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);

            order.getItems().add(orderItem);

            BigDecimal itemTotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        PaymentRequestDto paymentRequest = new PaymentRequestDto(savedOrder.getId(), savedOrder.getUserId(), savedOrder.getTotalAmount());
        PaymentResponseDto paymentResponse = paymentServiceClient.createPayment(paymentRequest);

        UserDto user = authServiceClient.getUserById(savedOrder.getUserId());

        OrderPlacedEvent event = new OrderPlacedEvent(savedOrder.getId(), savedOrder.getUserId(),
                savedOrder.getTotalAmount(), user.getEmail());
        orderEventProducer.sendOrderPlacedEvent(event);

        return new OrderPlacedResponse(savedOrder, paymentResponse.getClientSecret());
    }
}