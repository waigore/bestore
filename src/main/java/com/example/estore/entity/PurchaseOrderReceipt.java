package com.example.estore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderReceipt {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "orderId", nullable = false)
    private PurchaseOrder order;

    @Column(precision=8, scale=2)
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "orderReceipt", cascade = CascadeType.ALL)
    private List<PurchaseOrderReceiptItem> items;
}
