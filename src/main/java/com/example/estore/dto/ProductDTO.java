package com.example.estore.dto;

import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String productTypeCode;
    private String code;
    private String defaultDisplayName;
    private String status;
}
