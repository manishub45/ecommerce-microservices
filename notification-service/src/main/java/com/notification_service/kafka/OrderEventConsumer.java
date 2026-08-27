package com.notification_service.kafka;

import com.notification_service.dto.OrderPlacedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {

    @KafkaListener(topics = "order-placed-topic", groupId = "notification-group")
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        System.out.println("Notification: Order placed - Order ID: " + event.getOrderId()
                + ", User ID: " + event.getUserId()
                + ", Total Amount: " + event.getTotalAmount());


    }
}
