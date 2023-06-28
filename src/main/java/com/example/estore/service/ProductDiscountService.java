package com.example.estore.service;

import com.example.estore.dto.ProductDiscountDTO;
import com.example.estore.entity.Product;
import com.example.estore.entity.ProductDiscount;
import com.example.estore.exception.NoSuchObjectException;
import com.example.estore.mapper.ProductMapper;
import com.example.estore.repository.ProductDiscountRepository;
import com.example.estore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
public class ProductDiscountService {
    @Autowired
    private ProductDiscountRepository productDiscountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    public List<ProductDiscountDTO> getAllDiscounts() {
        List<ProductDiscount> productDiscounts = productDiscountRepository.findAll();
        return productDiscounts.stream().map(d -> productMapper.entityToDto(d)).toList();
    }

    public ProductDiscountDTO findByCode(String code) {
        ProductDiscount productDiscount = productDiscountRepository.findByCode(code);
        if (productDiscount == null) {
            throw new NoSuchObjectException("No such discount code '" + code + "'");
        }
        return productMapper.entityToDto(productDiscount);
    }

    public ProductDiscountDTO createDiscount(ProductDiscountDTO productDiscountDTO) {
        String productDiscountCode = productDiscountDTO.getCode();
        if (productDiscountCode == null || productDiscountCode.isBlank()) {
            throw new IllegalArgumentException("Discount code must be provided");
        }
        ProductDiscount existingDiscount = productDiscountRepository.findByCode(productDiscountCode);
        if (existingDiscount != null) {
            throw new IllegalStateException("Discount code '" + productDiscountCode + "' already exists");
        }

        String productCode = productDiscountDTO.getProductCode();
        Product product = productRepository.findByCode(productCode);
        if (product == null) {
            throw new NoSuchObjectException("No such product with code '" + productCode + "'");
        }

        BigDecimal productDiscountAmount = productDiscountDTO.getAmount() != null ?
                new BigDecimal(productDiscountDTO.getAmount()) : null;
        if ((productDiscountDTO.getPercentage() != null && productDiscountDTO.getPercentage() != 0) && productDiscountAmount != null) {
            throw new IllegalArgumentException("Discount percentage and amount cannot be both defined");
        }
        else if ((productDiscountDTO.getPercentage() == null || productDiscountDTO.getPercentage() == 0) && productDiscountAmount == null) {
            throw new IllegalArgumentException("Either discount percentage or amount should be defined");
        }

        ProductDiscount productDiscount = ProductDiscount.builder()
                .product(product)
                .code(productDiscountCode)
                .lineDiscount(productDiscountDTO.getLineDiscount())
                .percentage(productDiscountDTO.getPercentage())
                .amount(productDiscountAmount)
                .appliedNumber(productDiscountDTO.getAppliedNumber())
                .appliedTimes(productDiscountDTO.getAppliedTimes())
                .description(productDiscountDTO.getDescription())
                .startDateTime(Timestamp.valueOf(productDiscountDTO.getStartDateTime()))
                .endDateTime(Timestamp.valueOf(productDiscountDTO.getEndDateTime()))
                .build();

        ProductDiscount savedProductDiscount = productDiscountRepository.save(productDiscount);
        return productMapper.entityToDto(savedProductDiscount);
    }

    public void deleteDiscount(String code) {
        ProductDiscount productDiscount = productDiscountRepository.findByCode(code);
        if (productDiscount == null) {
            throw new IllegalArgumentException("No such discount code '" + "'");
        }
        productDiscountRepository.delete(productDiscount);
    }
}
