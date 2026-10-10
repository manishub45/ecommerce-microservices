package com.order_service.dto;

import java.math.BigDecimal;

public class BrandPriceDto {

    private Long brandId;
    private Long productId;
    private String productName;
    private String brandName;
    private BigDecimal price;

    public BrandPriceDto() {
    }

    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
