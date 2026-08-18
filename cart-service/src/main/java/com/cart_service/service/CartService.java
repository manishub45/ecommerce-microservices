package com.cart_service.service;

import com.cart_service.dto.AddToCartRequest;
import com.cart_service.entity.Cart;
import com.cart_service.entity.CartItem;
import com.cart_service.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart addToCart(String uuid, AddToCartRequest request) {

        Cart cart;

        // 1. Check UUID
        if (uuid == null || uuid.isEmpty()) {
            String newUuid  = UUID.randomUUID().toString();

            cart = new Cart();
            cart.setUuid(newUuid);

        } else {
            final String finalUuid = uuid;
            cart = cartRepository.findByUuid(uuid)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUuid(finalUuid);
                        return newCart;
                    });
        }

        // 2. Check if product already exists
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(request.getProductId());
            newItem.setBrandId(request.getBrandId());
            newItem.setQuantity(request.getQuantity());
            newItem.setPrice(request.getPrice());
            newItem.setCart(cart);


            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }
}
