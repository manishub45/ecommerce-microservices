package com.product_service.service;

import com.product_service.dto.CategoryDto;
import com.product_service.entity.Category;
import com.product_service.mapper.CategoryMapper;
import com.product_service.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDto> findAll() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> dtoList = new ArrayList<>();
        for(Category C : categories){
            CategoryDto categoryDto = CategoryMapper.convertCategorytoCategoryDto(C);
            dtoList.add(categoryDto);


        }

        return  dtoList;
    }

    @Override
    public CategoryDto findByCategorybyId(long id) {
        return null;
    }

    @Override
    public CategoryDto findByCategorybyName(String name) {
        return null;
    }
}
