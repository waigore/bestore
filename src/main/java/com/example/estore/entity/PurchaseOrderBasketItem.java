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

    private String itemReference;

    @ManyToOne
    @JoinColumn(name = "orderBasketId", nullable = false)
    private PurchaseOrderBasket orderBasket;

    private String productCode;

    private Integer quantity;
}
