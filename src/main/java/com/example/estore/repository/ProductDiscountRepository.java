package com.example.estore.repository;

import com.example.estore.entity.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {
    public ProductDiscount findByCode(String code);
}
