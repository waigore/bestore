package com.example.estore.repository;

import com.example.estore.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
    public ProductType findByType(String type);
}
