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

    @ManyToOne
    @JoinColumn(name = "orderReceiptId", nullable = false)
    private PurchaseOrderReceipt orderReceipt;

    @OneToOne
    @JoinColumn(name = "orderBasketItemId", nullable = false)
    private PurchaseOrderBasketItem orderBasketItem;

    @Column(precision=8, scale=2)
    private BigDecimal itemPrice;
}
