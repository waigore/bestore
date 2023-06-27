package com.example.estore.mapper;

import com.example.estore.dto.ProductDTO;
import com.example.estore.dto.ProductDiscountDTO;
import com.example.estore.dto.ProductPriceDTO;
import com.example.estore.dto.ProductTypeDTO;
import com.example.estore.entity.Product;
import com.example.estore.entity.ProductDiscount;
import com.example.estore.entity.ProductPrice;
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

    ProductPriceDTO entityToDto(ProductPrice productPrice);

    @Mapping(target="productCode", source="productDiscount.product.code")
    ProductDiscountDTO entityToDto(ProductDiscount productDiscount);
}
