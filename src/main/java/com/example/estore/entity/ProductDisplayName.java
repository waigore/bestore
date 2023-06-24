package com.example.estore.entity;

import jakarta.persistence.*;

@Entity
public class ProductDisplayName {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    private String locale;

    private String displayName;
}
