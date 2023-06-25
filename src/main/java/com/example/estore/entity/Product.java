package com.example.estore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    public enum Status {
        ACTIVE, DELETED;

        public List<String> strings() {
            return Arrays.stream(values()).map(s -> s.toString()).toList();
        }
    }

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "productTypeId", nullable = false)
    private ProductType productType;

    private String code;

    private String defaultDisplayName;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductPrice> prices;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductDiscount> discounts;
}
