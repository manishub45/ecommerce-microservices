package com.payment_service.dto;

import java.math.BigDecimal;

public class PaymentRequestDto {

    private Long orderId;
    private Long userId;
    private BigDecimal amount;

    public PaymentRequestDto() {
    }

    public PaymentRequestDto(Long orderId, Long userId, BigDecimal amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}