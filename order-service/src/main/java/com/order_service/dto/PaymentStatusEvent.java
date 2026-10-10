package com.order_service.dto;

public class PaymentStatusEvent {

    private Long orderId;
    private String status;
    private String userEmail;

    public PaymentStatusEvent() {
    }

    public PaymentStatusEvent(Long orderId, String status, String userEmail) {
        this.orderId = orderId;
        this.status = status;
        this.userEmail = userEmail;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
