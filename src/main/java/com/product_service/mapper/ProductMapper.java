package com.product_service.mapper;


import com.product_service.dto.ProductDto;

import com.product_service.entity.Product;
import org.modelmapper.ModelMapper;

public class ProductMapper {

    public static final ModelMapper mapper = new ModelMapper();

    public static ProductDto convertProdcutToDto(Product product){

        return mapper.map(product,ProductDto.class);

    }
}
