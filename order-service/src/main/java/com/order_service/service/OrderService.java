package com.order_service.service;

import com.order_service.client.AuthServiceClient;
import com.order_service.client.CartServiceClient;
import com.order_service.client.PaymentServiceClient;
import com.order_service.dto.*;
import com.order_service.entity.*;
import com.order_service.kafka.OrderEventProducer;
import com.order_service.repository.IdempotencyRepository;
import com.order_service.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CartServiceClient cartServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final OrderEventProducer orderEventProducer;
    private final AuthServiceClient authServiceClient;
    private final ProductPriceService productPriceService;
    private final IdempotencyRepository idempotencyRepository;

    public OrderService(OrderRepository orderRepository, CartServiceClient cartServiceClient,
                        PaymentServiceClient paymentServiceClient, OrderEventProducer orderEventProducer,
                        AuthServiceClient authServiceClient, ProductPriceService productPriceService,
                        IdempotencyRepository idempotencyRepository) {
        this.orderRepository = orderRepository;
        this.cartServiceClient = cartServiceClient;
        this.paymentServiceClient = paymentServiceClient;
        this.orderEventProducer = orderEventProducer;
        this.authServiceClient = authServiceClient;
        this.productPriceService = productPriceService;
        this.idempotencyRepository = idempotencyRepository;
    }

    public OrderPlacedResponse placeOrder(Long userId, String idempotencyKey, PlaceOrderRequest request) {

        // ===== STEP 0: Idempotency check, SABSE PEHLE =====
        IdempotencyRecord existingRecord = idempotencyRepository.findById(idempotencyKey).orElse(null);

        if (existingRecord != null) {
            if (existingRecord.getStatus() == IdempotencyStatus.COMPLETED) {
                Order existingOrder = orderRepository.findById(existingRecord.getOrderId())
                        .orElseThrow(() -> new IllegalStateException("Order not found for existing idempotency key"));
                return buildResponse(existingOrder, null);
            } else {
                throw new IllegalStateException("A previous request with this key is still processing. Please retry shortly.");
            }
        }

        IdempotencyRecord record = new IdempotencyRecord(idempotencyKey, IdempotencyStatus.PROCESSING);
        idempotencyRepository.save(record);

        // ===== STEP 1: Cart se lo =====
        CartResponseDto cart = cartServiceClient.getCart(request.getCartUuid());

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // ===== STEP 2: brandIds =====
        List<Long> brandIds = cart.getItems().stream()
                .map(CartItemDto::getBrandId)
                .distinct()
                .toList();

        // ===== STEP 3: live price =====
        List<BrandPriceDto> livePrices = productPriceService.fetchBrandPrices(brandIds);

        Map<Long, BrandPriceDto> priceByBrandId = livePrices.stream()
                .collect(Collectors.toMap(BrandPriceDto::getBrandId, Function.identity()));

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PLACED);
        order.setOrderDate(LocalDateTime.now());
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItemDto cartItem : cart.getItems()) {

            if (cartItem.getQuantity() == null || cartItem.getQuantity() <= 0) {
                throw new IllegalArgumentException("Invalid quantity for brandId " + cartItem.getBrandId());
            }

            BrandPriceDto live = priceByBrandId.get(cartItem.getBrandId());
            if (live == null) {
                throw new IllegalStateException("Price not found for brandId " + cartItem.getBrandId());
            }

            if (!live.getProductId().equals(cartItem.getProductId())) {
                throw new IllegalArgumentException("brandId " + cartItem.getBrandId()
                        + " does not belong to productId " + cartItem.getProductId());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(live.getProductId());
            orderItem.setBrandId(live.getBrandId());
            orderItem.setProductName(live.getProductName());
            orderItem.setBrandName(live.getBrandName());
            orderItem.setPrice(live.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);

            order.getItems().add(orderItem);

            BigDecimal itemTotal = live.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        record.setOrderId(savedOrder.getId());
        idempotencyRepository.save(record);

        PaymentRequestDto paymentRequest = new PaymentRequestDto(savedOrder.getId(), savedOrder.getUserId(), savedOrder.getTotalAmount());
        PaymentResponseDto paymentResponse = paymentServiceClient.createPayment(paymentRequest, idempotencyKey);

        UserDto user = authServiceClient.getUserById(savedOrder.getUserId());

        OrderPlacedEvent event = new OrderPlacedEvent(savedOrder.getId(), savedOrder.getUserId(),
                savedOrder.getTotalAmount(), user.getEmail());
        orderEventProducer.sendOrderPlacedEvent(event);

        record.setStatus(IdempotencyStatus.COMPLETED);
        idempotencyRepository.save(record);

        return buildResponse(savedOrder, paymentResponse.getClientSecret());
    }

    private OrderPlacedResponse buildResponse(Order order, String clientSecret) {
        return new OrderPlacedResponse(order, clientSecret);
    }

    public void updateOrderStatusFromPayment(Long orderId, String paymentStatus) {

        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            log.warn("Order {} not found, skipping status update", orderId);
            return;
        }

        switch (paymentStatus) {
            case "SUCCESS" -> {
                if (order.getStatus() != OrderStatus.PLACED) {
                    log.warn("Order {} not in PLACED state, skipping SUCCESS update", orderId);
                    return;
                }
                order.setStatus(OrderStatus.CONFIRMED);
            }
            case "FAILED" -> {
                if (order.getStatus() != OrderStatus.PLACED) {
                    log.warn("Order {} not in PLACED state, skipping FAILED update", orderId);
                    return;
                }
                order.setStatus(OrderStatus.CANCELLED);
            }
            case "REFUNDED" -> {   // naya case
                if (order.getStatus() != OrderStatus.CANCELLATION_REQUESTED) {
                    log.warn("Order {} not in CANCELLATION_REQUESTED state, skipping REFUNDED update", orderId);
                    return;
                }
                order.setStatus(OrderStatus.CANCELLED);
            }
            default -> {
                log.warn("Unknown payment status: {} for order {}", paymentStatus, orderId);
                return;
            }
        }

        orderRepository.save(order);
    }

    public OrderCancelResponse cancelOrder(Long orderId, Long userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found with id: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new IllegalStateException("This order does not belong to the current user");
        }

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Order cannot be cancelled. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLATION_REQUESTED);
        orderRepository.save(order);

        try {
            paymentServiceClient.refundPayment(orderId);
        } catch (Exception e) {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            throw new IllegalStateException("Failed to initiate refund. Please try again later.");
        }

        return new OrderCancelResponse(
                order.getId(),
                order.getStatus().name(),
                "Your cancellation request has been received. Refund will be processed shortly."
        );
    }
}