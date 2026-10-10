package com.payment_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment_service.dto.PaymentStatusEvent;
import com.payment_service.entity.OutboxEvent;
import com.payment_service.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.payment-status}")
    private String paymentStatusTopic;

    public PaymentEventProducer(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    public void sendPaymentStatusEvent(PaymentStatusEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            OutboxEvent outboxEvent = new OutboxEvent(paymentStatusTopic, payload);
            outboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize PaymentStatusEvent", e);
        }
    }
}