package com.example.estore.controller;

import com.example.estore.dto.ProductDTO;
import com.example.estore.entity.Product;
import com.example.estore.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import static org.mockito.BDDMockito.given;

@WebMvcTest
public class ProductControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

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
                .andExpect(jsonPath("$.size()", is(allProducts.size())));
    }
}
