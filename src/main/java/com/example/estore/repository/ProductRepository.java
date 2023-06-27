package com.example.estore.repository;

import com.example.estore.entity.Product;
import com.example.estore.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    public Product findByCode(String code);

    public Product findByCodeAndStatus(String code, Product.Status status);

    @Query("select p from Product p where p.status = com.example.estore.entity.Product$Status.ACTIVE")
    public List<Product> findActiveProducts();
    public List<Product> findByProductType(ProductType productType);
}
