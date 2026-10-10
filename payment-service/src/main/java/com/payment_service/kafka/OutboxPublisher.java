package com.payment_service.kafka;

import com.payment_service.entity.OutboxEvent;
import com.payment_service.entity.OutboxStatus;
import com.payment_service.repository.OutboxRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(OutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        List<OutboxEvent> pendingEvents = outboxRepository.findByStatus(OutboxStatus.PENDING);

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Found {} pending outbox events to publish", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getPayload()).get();

                event.setStatus(OutboxStatus.SENT);
                outboxRepository.save(event);

                log.info("Outbox event {} published to topic {}", event.getId(), event.getTopic());

            } catch (Exception e) {
                log.warn("Failed to publish outbox event {}, will retry later. Reason: {}",
                        event.getId(), e.getMessage());
            }
        }
    }
}
