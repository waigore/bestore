package com.example.estore.dto;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePurchaseOrderItemDTO {
    private String productCode;
    private Integer quantity;
}
