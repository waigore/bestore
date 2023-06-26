package com.example.estore.service;

import com.example.estore.dto.PurchaseOrderDTO;
import com.example.estore.entity.*;
import com.example.estore.repository.PurchaseOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class PurchaseOrderServiceTests {
    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @InjectMocks
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Test
    public void testFindOrderByIdentifierWorks() {
        PurchaseOrder order = PurchaseOrder.builder()
                .id(1L)
                .orderIdentifier("e2703b64-1d11-494f-8978-fadbed5cc47d")
                .customerIdentifier("790d0828-46c3-4a0a-a7b9-94e20895261e")
                .createdTime(Timestamp.from(Instant.now()))
                .status(PurchaseOrder.Status.CREATED)
                .orderBasket(PurchaseOrderBasket.builder()
                        .items(List.of(
                                PurchaseOrderBasketItem.builder()
                                        .productCode("SAMSUNG_TV_01_BLACK")
                                        .itemReference("e2703b64-1d11-494f-8978-fadbed5cc47d_1")
                                        .quantity(1)
                                        .build()
                        ))
                        .build()
                )
                .orderReceipt(PurchaseOrderReceipt.builder()
                        .totalPrice(new BigDecimal("10000.00"))
                        .items(List.of(
                                PurchaseOrderReceiptItem.builder()
                                        .orderBasketItemReference("e2703b64-1d11-494f-8978-fadbed5cc47d_1")
                                        .itemPrice(new BigDecimal("10000.00"))
                                        .build()
                        ))
                        .build())
                .build();
        given(purchaseOrderRepository.findByOrderIdentifier(any())).willReturn(order);

        PurchaseOrderDTO myOrderDTO = purchaseOrderService.findByOrderIdentifier("e2703b64-1d11-494f-8978-fadbed5cc47d");
        assertThat(myOrderDTO.getOrderIdentifier()).isEqualTo(order.getOrderIdentifier());
        assertThat(myOrderDTO.getCustomerIdentifier()).isEqualTo(order.getCustomerIdentifier());
        assertThat(myOrderDTO.getStatus()).isEqualTo(order.getStatus().toString());
        assertThat(myOrderDTO.getOrderBasket().getItems()).hasSameSizeAs(order.getOrderBasket().getItems());
        assertThat(myOrderDTO.getOrderReceipt().getItems()).hasSameSizeAs(order.getOrderReceipt().getItems());
    }
}
