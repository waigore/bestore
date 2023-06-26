package com.example.estore.dto;

import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePurchaseOrderDTO {
    private String customerIdentifier;
    List<CreatePurchaseOrderItemDTO> orderItems;
}
