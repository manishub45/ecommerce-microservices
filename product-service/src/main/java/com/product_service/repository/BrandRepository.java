package com.product_service.repository;

import com.product_service.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;



import com.product_service.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

    @Query("SELECT b FROM Brand b JOIN FETCH b.product WHERE b.id IN :ids")
    List<Brand> findAllWithProductByIdIn(@Param("ids") List<Integer> ids);
}
