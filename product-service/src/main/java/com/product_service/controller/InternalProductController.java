package com.product_service.controller;

import com.product_service.dto.BrandPriceDto;
import com.product_service.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/v1/product")
public class InternalProductController {

    private final ProductService productService;

    public InternalProductController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping("/brands/prices")
    public ResponseEntity<List<BrandPriceDto>> getBrandPrices(@RequestParam List<Integer> ids) {
        List<BrandPriceDto> prices = productService.getBrandPrices(ids);
        return ResponseEntity.ok(prices);
    }
}
