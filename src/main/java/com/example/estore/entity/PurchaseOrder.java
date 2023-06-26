package com.example.estore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {
    public enum Status {
        CREATED
    }

    @Id
    @GeneratedValue
    private Long id;

    private String orderIdentifier;

    private String customerIdentifier;

    private Timestamp createdTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private PurchaseOrderBasket orderBasket;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private PurchaseOrderReceipt orderReceipt;
}
