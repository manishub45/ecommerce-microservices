package com.order_service.dto;



import com.order_service.entity.Order;

public class OrderPlacedResponse {

    private Order order;
    private String clientSecret;

    public OrderPlacedResponse() {
    }

    public OrderPlacedResponse(Order order, String clientSecret) {
        this.order = order;
        this.clientSecret = clientSecret;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
