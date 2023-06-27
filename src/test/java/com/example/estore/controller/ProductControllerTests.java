package com.example.estore.controller;

import com.example.estore.dto.ProductDTO;
import com.example.estore.entity.Product;
import com.example.estore.service.ProductService;
import com.example.estore.service.PurchaseOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class ProductControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    ObjectMapper objectMapper;

    private List<ProductDTO> allProducts;


    @BeforeEach
    public void setUp() {
        allProducts = List.of(
                ProductDTO.builder()
                        .id(1L)
                        .code("SAMSUNG_TV_02_RED")
                        .defaultDisplayName("Samsung Smart TV Red")
                        .productTypeCode("SMTV")
                        .status(Product.Status.ACTIVE.toString())
                        .build()
        );
    }

    @Test
    public void testGetAllProductsWorks() throws Exception {
        given(productService.getAllProducts()).willReturn(allProducts);

        ResultActions response = mockMvc.perform(get("/api/products"));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.body.size()", is(allProducts.size())));
    }
}
