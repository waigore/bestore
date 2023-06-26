package com.example.estore.dto;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDTO {
    private Long id;
    private String orderIdentifier;
    private String customerIdentifier;
    private String createdTime;
    private String status;
    private PurchaseOrderBasketDTO orderBasket;
    private PurchaseOrderReceiptDTO orderReceipt;
}
