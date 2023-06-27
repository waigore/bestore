package com.example.estore.dto;

import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePurchaseOrderDTO {
    private List<CreatePurchaseOrderItemDTO> addedItems;
    private List<DeletePurchaseOrderItemDTO> deletedItems;
}
