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

    private String description;

    private Boolean autoApplied;

    private Boolean lineDiscount;

    private Integer percentage;

    private BigDecimal amount;

    private Integer appliedNumber;

    private Integer appliedTimes;

    private Timestamp startDateTime;

    private Timestamp endDateTime;
}
