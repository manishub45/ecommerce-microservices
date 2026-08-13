package com.product_service.service;

import com.product_service.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    public List<CategoryDto> findAll();
    public CategoryDto findByCategorybyId(long id);
    public CategoryDto findByCategorybyName(String name);
}
