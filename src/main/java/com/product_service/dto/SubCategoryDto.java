package com.product_service.dto;

import com.product_service.entity.Category;
import com.product_service.entity.Product;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

public class SubCategoryDto {


    private Integer id;
    private String name;




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
}
