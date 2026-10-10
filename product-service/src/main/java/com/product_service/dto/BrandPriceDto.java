package com.product_service.dto;


import java.math.BigDecimal;

public class BrandPriceDto {

    private Integer brandId;
    private Integer productId;
    private String productName;
    private String brandName;
    private BigDecimal price;

    public BrandPriceDto() {
    }

    public BrandPriceDto(Integer brandId, Integer productId, String productName,
                         String brandName, BigDecimal price) {
        this.brandId = brandId;
        this.productId = productId;
        this.productName = productName;
        this.brandName = brandName;
        this.price = price;
    }

    public Integer getBrandId() { return brandId; }
    public void setBrandId(Integer brandId) { this.brandId = brandId; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
