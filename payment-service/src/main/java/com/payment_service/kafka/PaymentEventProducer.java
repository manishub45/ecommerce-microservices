package com.payment_service.kafka;



import com.payment_service.dto.PaymentStatusEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.payment-status}")
    private String paymentStatusTopic;

    public PaymentEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentStatusEvent(PaymentStatusEvent event) {
        kafkaTemplate.send(paymentStatusTopic, event);
    }
}
