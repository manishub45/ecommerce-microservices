package com.order_service.dto;



import java.math.BigDecimal;

public class PaymentRequestDto {

    private Long orderId;
    private BigDecimal amount;

    public PaymentRequestDto() {
    }

    public PaymentRequestDto(Long orderId, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
