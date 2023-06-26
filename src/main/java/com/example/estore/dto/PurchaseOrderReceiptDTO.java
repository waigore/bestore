package com.example.estore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderReceiptDTO {
    @JsonIgnore
    private Long id;
    private String totalPrice;
    private List<PurchaseOrderReceiptItemDTO> items;
}
