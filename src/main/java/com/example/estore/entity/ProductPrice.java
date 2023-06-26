package com.example.estore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(precision=8, scale=2)
    private BigDecimal price;

    private Timestamp startDateTime;

    private Timestamp endDateTime;
}
