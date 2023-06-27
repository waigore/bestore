package com.example.estore.controller;

import com.example.estore.dto.ProductDiscountDTO;
import com.example.estore.dto.PurchaseOrderDTO;
import com.example.estore.dto.api.APIResponseDTO;
import com.example.estore.entity.Product;
import com.example.estore.service.ProductDiscountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class ProductDiscountController {
    private static final Logger LOG = LoggerFactory.getLogger(ProductDiscountController.class);

    @Autowired
    private ProductDiscountService productDiscountService;

    @GetMapping
    public APIResponseDTO<List<ProductDiscountDTO>> getAllDiscounts() {
        return APIResponseDTO.<List<ProductDiscountDTO>>builder()
                .body(productDiscountService.getAllDiscounts())
                .build();
    }

    @GetMapping("{code}")
    public ResponseEntity<APIResponseDTO<ProductDiscountDTO>> findByCode(@PathVariable("code") String code) {
        try {
            return ResponseEntity.ok(
                    APIResponseDTO.<ProductDiscountDTO>builder()
                            .body(productDiscountService.findByCode(code))
                            .build()
            );
        } catch (Exception e) {
            LOG.error("Exception finding discount by code", e);
            return ResponseEntity.badRequest()
                    .body(APIResponseDTO.<ProductDiscountDTO>builder()
                            .status(APIResponseDTO.Status.ERROR)
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public APIResponseDTO<ProductDiscountDTO> createDiscount(@RequestBody ProductDiscountDTO productDiscount) {
        return APIResponseDTO.<ProductDiscountDTO>builder()
                .body(productDiscountService.createDiscount(productDiscount))
                .build();
    }
}
