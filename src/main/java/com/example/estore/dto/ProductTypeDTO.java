package com.example.estore.dto;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeDTO {
    private Long id;
    private String type;
    private String typeDisplayName;
}
