package com.example.estore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderBasket {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "orderId", nullable = false)
    private PurchaseOrder order;

    @OneToMany(mappedBy = "orderBasket", cascade = CascadeType.ALL)
    private List<PurchaseOrderBasketItem> items;
}
