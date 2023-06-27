package com.example.estore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDiscountDTO {
    @JsonIgnore
    private Long id;
    private String code;
    private Boolean lineDiscount;
    private String productCode;
    private String description;
    private Integer percentage;
    private String amount;
    private Integer appliedNumber;
    private Integer appliedTimes;
    private String startDateTime;
    private String endDateTime;
}
