package com.example.estore.mapper;

import com.example.estore.dto.ProductDTO;
import com.example.estore.dto.ProductTypeDTO;
import com.example.estore.entity.Product;
import com.example.estore.entity.ProductType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface ProductMapper {

    @Mapping(target="productTypeCode", source="product.productType.type")
    ProductDTO entityToDto(Product product);

    ProductTypeDTO entityToDto(ProductType productType);
}
