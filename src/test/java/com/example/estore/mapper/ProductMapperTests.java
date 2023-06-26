package com.example.estore.mapper;

import com.example.estore.dto.ProductDTO;
import com.example.estore.dto.ProductPriceDTO;
import com.example.estore.entity.Product;
import com.example.estore.entity.ProductPrice;
import com.example.estore.entity.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ProductMapperTests {
    @Autowired
    ProductMapper productMapper;

    ProductType productType;

    Product product;

    @BeforeEach
    void setUp() {
        productType = ProductType.builder()
                .type("SMTV")
                .typeDisplayName("Smart TVs")
                .build();
        product = Product.builder()
                .id(1L)
                .code("LG_01_BLACK")
                .defaultDisplayName("LG Smart TV Black")
                .productType(productType)
                .status(Product.Status.ACTIVE)
                .build();
    }

    @Test
    void testMappingProductsWorks() {
        ProductDTO productDTO = productMapper.entityToDto(product);
        assertThat(productDTO.getId()).isEqualTo(product.getId());
        assertThat(productDTO.getProductTypeCode()).isEqualTo(productType.getType());
        assertThat(productDTO.getStatus()).isEqualTo(Product.Status.ACTIVE.toString());
    }

    @Test
    void testMappingProductPriesWorks() {
        ProductPrice productPrice = ProductPrice.builder()
                .product(product)
                .currency(ProductPrice.Currency.HKD)
                .price(new BigDecimal("5000.00"))
                .startDateTime(Timestamp.valueOf("2023-05-01 00:00:00"))
                .endDateTime(Timestamp.valueOf("2023-08-31 23:59:59"))
                .build();

        ProductPriceDTO productPriceDTO = productMapper.entityToDto(productPrice);
        assertThat(productPriceDTO.getProductId()).isEqualTo(product.getId());
        assertThat(productPriceDTO.getCurrency()).isEqualTo(ProductPrice.Currency.HKD.toString());
        assertThat(productPriceDTO.getStartDateTime()).isEqualTo("2023-05-01T00:00:00.000+08:00");
        assertThat(productPriceDTO.getEndDateTime()).isEqualTo("2023-08-31T23:59:59.000+08:00");
    }
}
