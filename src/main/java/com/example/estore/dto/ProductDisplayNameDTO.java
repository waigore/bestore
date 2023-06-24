package com.example.estore.dto;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDisplayNameDTO {
    private Long id;
    private String locale;
    private String displayName;
}
