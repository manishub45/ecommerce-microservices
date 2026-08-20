package com.order_service.service;



import com.order_service.client.CartServiceClient;
import com.order_service.dto.CartItemDto;
import com.order_service.dto.CartResponseDto;
import com.order_service.dto.PlaceOrderRequest;
import com.order_service.entity.Order;
import com.order_service.entity.OrderItem;
import com.order_service.entity.OrderStatus;
import com.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartServiceClient cartServiceClient;

    public OrderService(OrderRepository orderRepository, CartServiceClient cartServiceClient) {
        this.orderRepository = orderRepository;
        this.cartServiceClient = cartServiceClient;
    }

    public Order placeOrder(PlaceOrderRequest request) {

        // 1. Cart Service se cart mangwao (Feign Client se)
        CartResponseDto cart = cartServiceClient.getCart(request.getCartUuid());

        // 2. Naya Order banao
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.PLACED);
        order.setOrderDate(LocalDateTime.now());

        // 3. Cart ke har item ko OrderItem mein convert karo (SNAPSHOT lena)
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItemDto cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);

            order.getItems().add(orderItem);

            // Total amount calculate karo
            BigDecimal itemTotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);

        // 4. DB mein save karo
        return orderRepository.save(order);
    }
}
