package com.example.estore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderBasketItemDTO {
    @JsonIgnore
    private Long id;
    private String productCode;
    private String itemReference;
    private Integer quantity;
}
