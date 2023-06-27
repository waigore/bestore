package com.example.estore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderReceiptItemDiscountDTO {
    @JsonIgnore
    private Long id;
    private String productDiscountCode;
    private String discountedAmount;
}
