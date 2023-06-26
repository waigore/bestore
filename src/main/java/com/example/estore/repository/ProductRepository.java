package com.example.estore.repository;

import com.example.estore.entity.Product;
import com.example.estore.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    public Product findByCode(String code);

    public Product findByCodeAndStatus(String code, Product.Status status);
    public List<Product> findByProductType(ProductType productType);
}
