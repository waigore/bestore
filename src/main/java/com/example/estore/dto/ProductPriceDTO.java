package com.example.estore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceDTO {
    @JsonIgnore
    private Long id;
    @JsonIgnore
    private Long productId;
    private String currency;
    private String price;
    private String startDateTime;
    private String endDateTime;
}
