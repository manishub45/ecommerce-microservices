package com.payment_service.dto;



import com.payment_service.enums.PaymentStatus;

public class PaymentResponseDto {

    private Long paymentId;
    private PaymentStatus status;
    private String clientSecret;

    public PaymentResponseDto() {
    }

    public PaymentResponseDto(Long paymentId, PaymentStatus status, String clientSecret) {
        this.paymentId = paymentId;
        this.status = status;
        this.clientSecret = clientSecret;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
