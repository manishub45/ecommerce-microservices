package com.notification_service.kafka;

import com.notification_service.dto.OrderPlacedEvent;
import com.notification_service.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {

    private final EmailService emailService;

    public OrderEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "order-placed-topic", groupId = "notification-group", containerFactory = "orderKafkaListenerContainerFactory")
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {

        System.out.println("Notification: Order placed - Order ID: " + event.getOrderId()
                + ", User ID: " + event.getUserId()
                + ", Total Amount: " + event.getTotalAmount());

        emailService.sendOrderConfirmationEmail(event.getUserEmail(), event.getOrderId(), event.getTotalAmount());
    }
}