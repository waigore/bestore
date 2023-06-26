package com.example.estore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderReceiptItemDTO {
    @JsonIgnore
    private Long id;
    private String orderBasketItemReference;
    private String itemPrice;
}
