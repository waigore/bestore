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
public class ProductDiscount {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    private String code;

    @Builder.Default
    private Boolean lineDiscount = false;

    private String description;

    private Integer percentage;

    @Column(precision=8, scale=2)
    private BigDecimal amount;

    private Integer appliedNumber;

    private Integer appliedTimes;

    private Timestamp startDateTime;

    private Timestamp endDateTime;
}
