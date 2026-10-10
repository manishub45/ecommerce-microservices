package com.order_service.client;

import com.order_service.dto.BrandPriceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/internal/v1/product/brands/prices")
    List<BrandPriceDto> getBrandPrices(@RequestParam("ids") List<Long> ids);
}
