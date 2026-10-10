package com.product_service.service;

import com.product_service.dto.BrandPriceDto;
import com.product_service.dto.ProductDto;
import com.product_service.entity.Brand;
import com.product_service.entity.Product;
import com.product_service.exception.ResourceNotFoundException;
import com.product_service.mapper.ProductMapper;
import com.product_service.repository.BrandRepository;
import com.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceimpl implements ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    public ProductServiceimpl(ProductRepository productRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
    }

    @Override
    public List<ProductDto> searchProducts(String keyword) {
        List<Product> products = productRepository.searchProducts(keyword);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product p : products) {
            productDtos.add(ProductMapper.convertProdcutToDto(p));
        }
        return productDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandPriceDto> getBrandPrices(List<Integer> brandIds) {

        // 1. Khaali list aayi to khaali jawab
        if (brandIds == null || brandIds.isEmpty()) {
            return List.of();
        }

        // 2. Duplicate ids hatao
        List<Integer> uniqueIds = brandIds.stream().distinct().toList();

        // 3. DB se ek hi query mein laao
        List<Brand> brands = brandRepository.findAllWithProductByIdIn(uniqueIds);

        // 4. Koi id DB mein nahi mili? To chupchaap mat chhodo, error do
        if (brands.size() != uniqueIds.size()) {
            Set<Integer> foundIds = brands.stream()
                    .map(Brand::getId)
                    .collect(Collectors.toSet());

            List<Integer> missingIds = uniqueIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new ResourceNotFoundException("Brands not found for ids: " + missingIds);
        }

        // 5. Entity ko chhote DTO mein badlo
        return brands.stream()
                .map(b -> new BrandPriceDto(
                        b.getId(),
                        b.getProduct().getId(),
                        b.getProduct().getName(),
                        b.getName(),
                        b.getPrice()
                ))
                .toList();
    }
}