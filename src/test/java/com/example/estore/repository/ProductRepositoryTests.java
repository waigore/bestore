package com.example.estore.repository;

import com.example.estore.entity.Product;
import com.example.estore.entity.ProductType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = "classpath:db/product_type_data.sql")
public class ProductRepositoryTests {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @AfterEach
    void clearProducts() {
        productRepository.deleteAll();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(productRepository).isNotNull();
    }

    @Test
    void testCreateProductWorks() {
        ProductType smtvProductType = productTypeRepository.findByType("SMTV");

        String productCode = "SAMSUNG_TV_02_WHITE";
        Product product = Product.builder()
                .code(productCode)
                .defaultDisplayName("Samsung Smart TV White")
                .status(Product.Status.ACTIVE)
                .productType(smtvProductType)
                .build();
        productRepository.save(product);

        Product myProduct = productRepository.findByCode(productCode);
        assertThat(myProduct.getId()).isNotNull();
        assertThat(myProduct.getProductType()).isEqualTo(smtvProductType);
    }

    @Test
    void testFindProductsByTypeWorks() {
        ProductType smartphoneProductType = productTypeRepository.findByType("PHON");

        String productCode = "IPHONE_15_BLACK";
        Product product = Product.builder()
                .code(productCode)
                .defaultDisplayName("iPhone 15 Black")
                .status(Product.Status.ACTIVE)
                .productType(smartphoneProductType)
                .build();
        productRepository.save(product);

        List<Product> productList = productRepository.findByProductType(smartphoneProductType);
        assertThat(productList).isNotEmpty();
        assertThat(productList).hasSize(1);
    }

    @Test
    void testFindProductsByCodeAndStatusWorks() {
        ProductType smartphoneProductType = productTypeRepository.findByType("PHON");

        String productCode = "IPHONE_15_BLACK";
        Product product = Product.builder()
                .code(productCode)
                .defaultDisplayName("iPhone 15 Black")
                .status(Product.Status.ACTIVE)
                .productType(smartphoneProductType)
                .build();
        productRepository.save(product);

        Product foundProduct = productRepository.findByCodeAndStatus("IPHONE_15_BLACK", Product.Status.ACTIVE);
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getCode()).isEqualTo(productCode);

        foundProduct = productRepository.findByCodeAndStatus("IPHONE_15_BLACK", Product.Status.DELETED);
        assertThat(foundProduct).isNull();
    }
}
