package com.example.estore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderBasketDTO {
    @JsonIgnore
    private Long id;
    private List<PurchaseOrderBasketItemDTO> items;
}
