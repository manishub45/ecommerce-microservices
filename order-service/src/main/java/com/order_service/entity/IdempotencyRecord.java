package com.order_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyRecord {

    @Id
    private String idempotencyKey;

    private Long orderId;

    @Enumerated(EnumType.STRING)
    private IdempotencyStatus status;

    private LocalDateTime createdAt;

    public IdempotencyRecord() {
    }

    public IdempotencyRecord(String idempotencyKey, IdempotencyStatus status) {
        this.idempotencyKey = idempotencyKey;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public IdempotencyStatus getStatus() {
        return status;
    }

    public void setStatus(IdempotencyStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
