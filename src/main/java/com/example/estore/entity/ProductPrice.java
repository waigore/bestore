package com.example.estore.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
public class ProductPrice {
    public enum Currency {
        HKD
    }
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private BigDecimal price;

    private Timestamp startDateTime;

    private Timestamp endDateTime;
}
