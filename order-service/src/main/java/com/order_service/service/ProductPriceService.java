package com.order_service.service;

import com.order_service.client.ProductServiceClient;
import com.order_service.dto.BrandPriceDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductPriceService {

    private final ProductServiceClient productServiceClient;

    public ProductPriceService(ProductServiceClient productServiceClient) {
        this.productServiceClient = productServiceClient;
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "getBrandPricesFallback")
    public List<BrandPriceDto> fetchBrandPrices(List<Long> brandIds) {
        return productServiceClient.getBrandPrices(brandIds);
    }

    private List<BrandPriceDto> getBrandPricesFallback(List<Long> brandIds, Throwable t) {
        throw new IllegalStateException("Pricing service is currently unavailable. Please try again shortly.");
    }
}
