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
public class PurchaseOrderReceiptItem {
    @Id
    @GeneratedValue
    private Long id;

    private String orderBasketItemReference;

    @ManyToOne
    @JoinColumn(name = "orderReceiptId", nullable = false)
    private PurchaseOrderReceipt orderReceipt;

    @Column(precision=8, scale=2)
    private BigDecimal itemPrice;
}
