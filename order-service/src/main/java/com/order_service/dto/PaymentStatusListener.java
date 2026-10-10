package com.order_service.kafka;

import com.order_service.dto.PaymentStatusEvent;
import com.order_service.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PaymentStatusListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentStatusListener.class);

    private final OrderService orderService;

    public PaymentStatusListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "${kafka.topic.payment-status}", groupId = "order-group")
    public void handlePaymentStatus(PaymentStatusEvent event) {
        log.info("Payment status event received: orderId={}, status={}", event.getOrderId(), event.getStatus());
        orderService.updateOrderStatusFromPayment(event.getOrderId(), event.getStatus());
    }
}
