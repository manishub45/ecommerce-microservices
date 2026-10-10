package com.payment_service.repository;

import com.payment_service.entity.OutboxEvent;
import com.payment_service.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatus(OutboxStatus status);
}
