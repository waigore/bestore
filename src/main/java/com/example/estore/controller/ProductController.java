package com.example.estore.controller;

import com.example.estore.dto.ProductDTO;
import com.example.estore.dto.api.APIResponseDTO;
import com.example.estore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public APIResponseDTO<List<ProductDTO>> getAllProducts() {
        return APIResponseDTO.<List<ProductDTO>>builder()
                .body(productService.getAllProducts()).build();
    }

    @GetMapping("/active")
    public APIResponseDTO<List<ProductDTO>> getAllActiveProducts() {
        return APIResponseDTO.<List<ProductDTO>>builder()
                .body(productService.getAllActiveProducts()).build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public APIResponseDTO<ProductDTO> createProduct(@RequestBody ProductDTO product) {
        return APIResponseDTO.<ProductDTO>builder()
                        .body(productService.saveProduct(product))
                .build();
    }

    @DeleteMapping("{code}")
    public ResponseEntity<APIResponseDTO<String>> deleteProduct(@PathVariable String code) {
        try {
            productService.deleteProduct(code);
            return ResponseEntity.ok(APIResponseDTO.<String>builder().build());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
