package com.order_service.dto;

import java.math.BigDecimal;

public class OrderPlacedEvent {

    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;

    public OrderPlacedEvent() {
    }

    public OrderPlacedEvent(Long orderId, Long userId, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
