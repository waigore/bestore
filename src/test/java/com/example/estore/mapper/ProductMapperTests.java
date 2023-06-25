package com.example.estore.mapper;

import com.example.estore.dto.ProductDTO;
import com.example.estore.entity.Product;
import com.example.estore.entity.ProductType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ProductMapperTests {
    @Autowired
    ProductMapper productMapper;

    @Test
    void testMappingProductsWorks() {
        ProductType productType = ProductType.builder()
                .type("SMTV")
                .typeDisplayName("Smart TVs")
                .build();
        Product product = Product.builder()
                .code("LG_01_BLACK")
                .defaultDisplayName("LG Smart TV Black")
                .productType(productType)
                .status(Product.Status.ACTIVE)
                .build();

        ProductDTO productDTO = productMapper.entityToDto(product);
        assertThat(productDTO.getProductTypeCode()).isEqualTo(productType.getType());
        assertThat(productDTO.getStatus()).isEqualTo(Product.Status.ACTIVE.toString());
    }

    @Test
    void testMappingProductWithNamesWorks() {
        String displayName = "iPhone 15 Black";
        ProductType productType = ProductType.builder()
                .type("PHON")
                .typeDisplayName("Smartphones")
                .build();
        ProductDisplayName name1 = ProductDisplayName.builder()
                .locale("EN")
                .displayName(displayName)
                .build();
        Product product = Product.builder()
                .displayNames(List.of(name1))
                .productType(productType)
                .build();

        ProductDTO productDTO = productMapper.entityToDto(product);
        assertThat(productDTO.getStatus()).isNull();
        assertThat(productDTO.getDisplayNames()).isNotEmpty();
        assertThat(productDTO.getDisplayNames()).hasSize(1);

        ProductDisplayNameDTO displayNameDTO = productDTO.getDisplayNames().get(0);
        assertThat(displayNameDTO.getLocale()).isEqualTo("EN");
        assertThat(displayNameDTO.getDisplayName()).isEqualTo(displayName);
    }
}
