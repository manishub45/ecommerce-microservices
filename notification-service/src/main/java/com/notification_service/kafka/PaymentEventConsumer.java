package com.notification_service.kafka;



import com.notification_service.dto.PaymentStatusEvent;
import com.notification_service.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {

    private final EmailService emailService;

    public PaymentEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "payment-status-topic", groupId = "notification-group", containerFactory = "paymentKafkaListenerContainerFactory")
    public void handlePaymentStatusEvent(PaymentStatusEvent event) {

        System.out.println("Notification: Payment status - Order ID: " + event.getOrderId()
                + ", Status: " + event.getStatus());

        emailService.sendPaymentStatusEmail(event.getUserEmail(), event.getOrderId(), event.getStatus());
    }
}