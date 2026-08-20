package com.cart_service.controller;

import com.cart_service.dto.AddToCartRequest;
import com.cart_service.entity.Cart;
import com.cart_service.service.CartService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @RequestHeader(value = "X-CART-ID", required = false) String uuid,
            @RequestBody AddToCartRequest request
    ) {

        Cart cart = cartService.addToCart(uuid, request);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-CART-ID", cart.getUuid());

        return ResponseEntity.ok()
                .headers(headers)
                .body("Product added to cart");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Cart> getCart(@PathVariable String uuid) {
        Cart cart = cartService.getCartByUuid(uuid);
        return ResponseEntity.ok(cart);
    }
}
