package com.example.estore.controller;

import com.example.estore.dto.*;
import com.example.estore.entity.*;
import com.example.estore.mapper.PurchaseOrderMapper;
import com.example.estore.service.ProductDiscountService;
import com.example.estore.service.ProductService;
import com.example.estore.service.PurchaseOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class PurchaseOrderControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private PurchaseOrderService purchaseOrderService;

    @MockBean
    private ProductDiscountService productDiscountService;

    @Autowired
    ObjectMapper objectMapper;


    PurchaseOrderDTO purchaseOrderDTO;

    @BeforeEach
    void setUp() {
        purchaseOrderDTO = PurchaseOrderDTO.builder()
                .id(1L)
                .orderIdentifier("e2703b64-1d11-494f-8978-fadbed5cc47d")
                .customerIdentifier("790d0828-46c3-4a0a-a7b9-94e20895261e")
                .createdTime(Timestamp.from(Instant.now()).toString())
                .status(PurchaseOrder.Status.CREATED.toString())
                .orderBasket(PurchaseOrderBasketDTO.builder()
                        .items(List.of(
                                PurchaseOrderBasketItemDTO.builder()
                                        .productCode("SAMSUNG_TV_01_BLACK")
                                        .itemReference("e2703b64-1d11-494f-8978-fadbed5cc47d_1")
                                        .quantity(1)
                                        .build()
                        ))
                        .build())
                .orderReceipt(PurchaseOrderReceiptDTO.builder()
                        .totalPrice("10000.00")
                        .items(List.of(
                                PurchaseOrderReceiptItemDTO.builder()
                                        .orderBasketItemReference("e2703b64-1d11-494f-8978-fadbed5cc47d_1")
                                        .itemPrice("10000.00")
                                        .build()
                        ))
                        .build())
                .build();
    }

    @Test
    void testFindOrderByIdentifierWorks() throws Exception {
        given(purchaseOrderService.findByOrderIdentifier(any())).willReturn(purchaseOrderDTO);

        ResultActions response = mockMvc.perform(get("/api/orders/" + purchaseOrderDTO.getOrderIdentifier()));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.body.orderIdentifier", is(purchaseOrderDTO.getOrderIdentifier())));
    }

    @Test
    public void testCreateOrdersWorks() throws Exception {
        given(purchaseOrderService.createOrder(any())).willReturn(purchaseOrderDTO);

        CreatePurchaseOrderDTO createPurchaseOrder = CreatePurchaseOrderDTO.builder()
                .orderItems(List.of(
                        CreatePurchaseOrderItemDTO.builder()
                                .productCode("SAMSUNG_TV_01_BLACK")
                                .quantity(1)
                                .build()
                ))
                .customerIdentifier("790d0828-46c3-4a0a-a7b9-94e20895261e")
                .build();

        ResultActions response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPurchaseOrder)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("OK")));
    }

    @Test
    public void testUpdateOrdersWorks() throws Exception {
        given(purchaseOrderService.updateOrder(any(), any())).willReturn(purchaseOrderDTO);

        UpdatePurchaseOrderDTO updatePurchaseOrder = UpdatePurchaseOrderDTO.builder()
                .addedItems(List.of(
                        CreatePurchaseOrderItemDTO.builder()
                                .productCode("SAMSUNG_TV_01_BLACK")
                                .quantity(3)
                                .build()
                ))
                .deletedItems(List.of(
                        DeletePurchaseOrderItemDTO.builder()
                                .itemReference("790d0828-46c3-4a0a-a7b9-94e20895261e")
                                .build()
                ))
                .build();

        ResultActions response = mockMvc.perform(put("/api/orders/" + purchaseOrderDTO.getOrderIdentifier())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePurchaseOrder)));

        response.andDo(print())
                .andExpect(jsonPath("$.status", is("OK")));
    }
}
