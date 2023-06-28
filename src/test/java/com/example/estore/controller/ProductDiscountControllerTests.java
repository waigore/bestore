package com.example.estore.controller;

import com.example.estore.dto.ProductDiscountDTO;
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

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class ProductDiscountControllerTests {
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

    private ProductDiscountDTO productDiscountDTO;

    @BeforeEach
    void setUp() {
        productDiscountDTO = ProductDiscountDTO.builder()
                .productCode("SAMSUNG_TV_01_BLACK")
                .code("500OFFSAMSUNG")
                .lineDiscount(true)
                .description("$500 off Samsung TV purchase")
                .amount("500.00")
                .appliedNumber(1)
                .appliedTimes(1)
                .startDateTime("2023-05-01 00:00:00")
                .endDateTime("2023-05-31 23:59:59")
                .build();
    }

    @Test
    public void testGetAllDiscountsWorks() throws Exception {
        List<ProductDiscountDTO> productDiscountDTOList = List.of(
                productDiscountDTO
        );
        given(productDiscountService.getAllDiscounts()).willReturn(productDiscountDTOList);

        ResultActions response = mockMvc.perform(get("/api/discounts"));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.body.size()", is(productDiscountDTOList.size())));
    }

    @Test
    public void testGetDiscountByCodeWorks() throws Exception {
        given(productDiscountService.findByCode(any())).willReturn(productDiscountDTO);

        ResultActions response = mockMvc.perform(get("/api/discounts/" + productDiscountDTO.getCode()));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.body.code", is(productDiscountDTO.getCode())));
    }

    @Test
    public void testCreateDiscountWorks() throws Exception {
        given(productDiscountService.createDiscount(any())).willReturn(productDiscountDTO);

        ResultActions response = mockMvc.perform(post("/api/discounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDiscountDTO)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("OK")));
    }

    @Test
    public void testDeleteDiscountWorks() throws Exception {
        doNothing().when(productDiscountService).deleteDiscount(any());

        ResultActions response = mockMvc.perform(delete("/api/discounts/" + productDiscountDTO.getCode()));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.status", is("OK")));
    }
}
