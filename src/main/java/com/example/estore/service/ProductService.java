package com.example.estore.service;

import com.example.estore.dto.ProductDTO;
import com.example.estore.dto.ProductTypeDTO;
import com.example.estore.entity.Product;
import com.example.estore.entity.ProductPrice;
import com.example.estore.entity.ProductType;
import com.example.estore.mapper.ProductMapper;
import com.example.estore.repository.ProductRepository;
import com.example.estore.repository.ProductTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private ProductMapper productMapper;

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(p -> productMapper.entityToDto(p)).collect(Collectors.toList());
    }

    public List<ProductDTO> getAllActiveProducts() {
        List<Product> products = productRepository.findActiveProducts();
        return products.stream().map(p -> productMapper.entityToDto(p)).collect(Collectors.toList());
    }

    public List<ProductTypeDTO> getAllProductTypes() {
        List<ProductType> productTypes = productTypeRepository.findAll();
        return productTypes.stream().map(pt -> productMapper.entityToDto(pt)).collect(Collectors.toList());
    }

    private void validateProductBeforeSave(ProductDTO productDTO, ProductType productType) {
        if (productDTO.getProductTypeCode() == null || productDTO.getProductTypeCode().isBlank()) {
            throw new IllegalArgumentException("Product type code is not provided");
        }

        if (productType == null) {
            throw new IllegalArgumentException("Product type code '" + productDTO.getProductTypeCode() + "' does not exist");
        }

        if (productDTO.getCode() == null || productDTO.getCode().isBlank()) {
            throw new IllegalArgumentException("Product code is not provided");
        }

        Product existingProduct = productRepository.findByCode(productDTO.getCode());
        if (existingProduct != null) {
            throw new IllegalArgumentException("Product with code '" + productDTO.getCode() + "' already exists");
        }
    }

    @Transactional
    public void deleteProduct(String code) {
        Product product = productRepository.findByCode(code);

        if (product == null) {
            throw new IllegalArgumentException("No such product with code '" + code + "'");
        }

        if (product.getStatus().equals(Product.Status.DELETED)) {
            throw new IllegalStateException("Product '" + code + "' already deleted");
        }

        product.setStatus(Product.Status.DELETED);
        productRepository.save(product);
    }

    @Transactional
    public ProductDTO saveProduct(ProductDTO productDTO) {
        ProductType productType = productTypeRepository.findByType(productDTO.getProductTypeCode());

        validateProductBeforeSave(productDTO, productType);

        Product product = Product.builder()
                .id(productDTO.getId())
                .code(productDTO.getCode())
                .status(productDTO.getStatus() != null && !productDTO.getStatus().isEmpty() ?
                        Product.Status.valueOf(productDTO.getStatus()) :
                        Product.Status.ACTIVE)
                .productType(productType)
                .defaultDisplayName(productDTO.getDefaultDisplayName())
                .build();

        if (productDTO.getPrices() != null) {
            List<ProductPrice> productPrices = productDTO.getPrices().stream()
                    .map(priceDTO -> ProductPrice.builder()
                            .product(product)
                            .price(new BigDecimal(priceDTO.getPrice()))
                            .currency(ProductPrice.Currency.valueOf(priceDTO.getCurrency()))
                            .startDateTime(Timestamp.valueOf(priceDTO.getStartDateTime()))
                            .endDateTime(Timestamp.valueOf(priceDTO.getEndDateTime()))
                            .build())
                    .collect(Collectors.toList());
            product.setPrices(productPrices);
        }

        Product savedProduct = productRepository.save(product);
        productDTO.setId(savedProduct.getId());
        return productDTO;
    }
}
