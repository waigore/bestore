package com.example.estore.repository;

import com.example.estore.entity.PurchaseOrder;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = {
        "classpath:db/product_type_data.sql",
        "classpath:db/product_data.sql",
        "classpath:db/order_data.sql"
})
public class PurchaseOrderRepositoryTests {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(purchaseOrderRepository).isNotNull();
    }

    @Test
    void testGetOrderWorks() {
        PurchaseOrder order = purchaseOrderRepository.findByOrderIdentifier("e2703b64-1d11-494f-8978-fadbed5cc47d");
        assertThat(order.getId()).isEqualTo(1L);
    }
}
