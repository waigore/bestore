package com.example.estore.dto;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeletePurchaseOrderItemDTO {
    private String itemReference;
}
