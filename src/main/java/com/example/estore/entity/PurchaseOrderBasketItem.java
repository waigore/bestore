package com.example.estore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderBasketItem {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orderBasketId", nullable = false)
    private PurchaseOrderBasket orderBasket;

    @OneToOne(mappedBy = "orderBasketItem")
    private PurchaseOrderReceiptItem orderReceiptItem;

    private String productCode;

    private Integer quantity;
}
