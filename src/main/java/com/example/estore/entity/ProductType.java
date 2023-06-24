package com.example.estore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductType {
    @Id
    @GeneratedValue
    private Long id;
    private String type;
    private String typeDisplayName;

    @OneToMany(mappedBy = "productType")
    private List<Product> products;

}
