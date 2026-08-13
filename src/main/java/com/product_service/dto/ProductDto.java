package com.product_service.dto;

import com.product_service.entity.Brand;
import com.product_service.entity.SubCategory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.LinkedHashSet;
import java.util.Set;

public class ProductDto {

    private Integer id;
    private String name;
    private SubCategoryDto subCategoryDto;
    private Set<BrandDto> brandDtos = new LinkedHashSet<>();





    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SubCategoryDto getSubCategoryDto() {
        return subCategoryDto;
    }

    public void setSubCategoryDto(SubCategoryDto subCategoryDto) {
        this.subCategoryDto = subCategoryDto;
    }

    public Set<BrandDto> getBrandDtos() {
        return brandDtos;
    }

    public void setBrandDtos(Set<BrandDto> brandDtos) {
        this.brandDtos = brandDtos;
    }
}
