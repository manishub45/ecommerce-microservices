package com.order_service.dto;

public class PlaceOrderRequest {

    private String cartUuid;

    public String getCartUuid() {
        return cartUuid;
    }

    public void setCartUuid(String cartUuid) {
        this.cartUuid = cartUuid;
    }
}