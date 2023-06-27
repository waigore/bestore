package com.example.estore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderReceiptItemDiscount {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "orderReceiptItemId", nullable = false)
    private PurchaseOrderReceiptItem orderReceiptItem;

    private String productDiscountCode;

    @Column(precision = 8, scale = 2)
    private BigDecimal discountedAmount;
}
