package com.example.estore.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class ProductType {
    @Id
    @GeneratedValue
    private Long id;
    private String type;
    private String typeDisplayName;

    @OneToMany(mappedBy = "productType")
    private List<Product> products;
}
