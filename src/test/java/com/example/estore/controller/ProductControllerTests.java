package com.example.estore.controller;

import com.example.estore.dto.ProductDTO;
import com.example.estore.entity.Product;
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
public class ProductControllerTests {
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
                        .build(),
                ProductDTO.builder()
                        .id(2L)
                        .code("SAMSUNG_TV_03_BLUE")
                        .defaultDisplayName("Samsung Smart TV Blue")
                        .productTypeCode("SMTV")
                        .status(Product.Status.DELETED.toString())
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

    @Test
    public void testGetAllActiveProductsWorks() throws Exception {
        given(productService.getAllActiveProducts())
                .willAnswer(i -> allProducts.stream().filter(p -> p.getStatus().equals(Product.Status.ACTIVE.toString())).toList()
                );

        ResultActions response = mockMvc.perform(get("/api/products/active"));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.body.size()", is(1)));
    }

    @Test
    public void testCreateProductsWorks() throws Exception {
        ProductDTO productDTO = ProductDTO.builder()
                .code("SAMSUNG_TV_01_BLACK")
                .productTypeCode("SMTV")
                .status(Product.Status.ACTIVE.toString())
                .defaultDisplayName("Samsung TV Black")
                .build();
        given(productService.saveProduct(productDTO)).willAnswer(i -> i.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("OK")));
    }

    @Test
    public void testDeleteProductsWorks() throws Exception {
        doNothing().when(productService).deleteProduct(any());

        ResultActions response = mockMvc.perform(delete("/api/products/" + "SAMSUNG_TV_01_BLACK"));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.status", is("OK")));
    }
}
