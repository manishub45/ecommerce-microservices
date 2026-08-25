package com.order_service.dto;

public class PaymentResponseDto {

    private Long paymentId;
    private String status;
    private String clientSecret;

    public PaymentResponseDto() {
    }

    public PaymentResponseDto(Long paymentId, String status, String clientSecret) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
