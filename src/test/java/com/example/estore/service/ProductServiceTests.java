package com.example.estore.service;

import com.example.estore.dto.ProductDTO;
import com.example.estore.dto.ProductPriceDTO;
import com.example.estore.dto.ProductTypeDTO;
import com.example.estore.entity.Product;
import com.example.estore.entity.ProductPrice;
import com.example.estore.entity.ProductType;
import com.example.estore.repository.ProductRepository;
import com.example.estore.repository.ProductTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductTypeRepository productTypeRepository;

    @InjectMocks
    @Autowired
    private ProductService productService;

    private List<Product> allProducts;

    private List<ProductType> allProductTypes;

    private ProductDTO productDTOToSave;

    @BeforeEach
    public void setUp() {
        allProductTypes = List.of(
                ProductType.builder()
                        .type("SMTV")
                        .typeDisplayName("Smart TVs")
                        .build(),
                ProductType.builder()
                        .type("PHON")
                        .typeDisplayName("Smartphones")
                        .build()
        );

        allProducts = List.of(
                Product.builder()
                        .code("SAMSUNG_TV_01_GREY")
                        .productType(allProductTypes.get(0))
                        .defaultDisplayName("Samsung Smart TV Grey")
                        .status(Product.Status.ACTIVE)
                        .build()
        );

        productDTOToSave = ProductDTO.builder()
                .code("LG_TV_01_WHITE")
                .productTypeCode("SMTV")
                .defaultDisplayName("LG Smart TV White")
                .status(Product.Status.ACTIVE.toString())
                .build();
    }

    @Test
    public void testGetAllProductsWorks() {
        given(productRepository.findAll()).willReturn(allProducts);

        List<ProductDTO> allProductDTOs = productService.getAllProducts();
        assertThat(allProductDTOs).isNotEmpty();
        assertThat(allProductDTOs).hasSize(1);

        ProductDTO productDTO = allProductDTOs.get(0);
        assertThat(productDTO.getStatus()).isEqualTo(Product.Status.ACTIVE.toString());
        assertThat(productDTO.getCode()).isEqualTo("SAMSUNG_TV_01_GREY");
        assertThat(productDTO.getProductTypeCode()).isEqualTo("SMTV");
    }

    @Test
    public void testGetAllProductTypesWorks() {
        given(productTypeRepository.findAll()).willReturn(allProductTypes);

        List<ProductTypeDTO> allProductTypeDTOs = productService.getAllProductTypes();
        assertThat(allProductTypeDTOs).isNotEmpty();
        assertThat(allProductTypeDTOs).hasSize(2);

        ProductTypeDTO productTypeDTO = allProductTypeDTOs.get(0);
        assertThat(productTypeDTO.getType()).isEqualTo("SMTV");
        assertThat(productTypeDTO.getTypeDisplayName()).isEqualTo("Smart TVs");
    }

    @Test
    public void testSaveProductWorks() {
        List<Product> myProducts = new ArrayList();
        given(productTypeRepository.findByType("SMTV")).willReturn(allProductTypes.get(0));
        given(productRepository.save(any())).willAnswer(i -> {
            Product p = i.getArgument(0);
            if (p.getId() == null)
                p.setId((long)(myProducts.size()+1));
            myProducts.add(p);
            return p;
        });

        productService.saveProduct(productDTOToSave);
        assertThat(myProducts).hasSize(1);

        Product product = myProducts.get(0);
        assertThat(product.getId()).isEqualTo(1L);
    }

    @Test
    public void testSaveProductWithPricesWorks() {
        List<Product> myProducts = new ArrayList();
        given(productTypeRepository.findByType("SMTV")).willReturn(allProductTypes.get(0));
        given(productRepository.save(any())).willAnswer(i -> {
            Product p = i.getArgument(0);
            if (p.getId() == null)
                p.setId((long)(myProducts.size()+1));
            myProducts.add(p);
            return p;
        });

        productDTOToSave = ProductDTO.builder()
                .code("LG_TV_01_WHITE")
                .productTypeCode("SMTV")
                .defaultDisplayName("LG Smart TV White")
                .prices(List.of(
                        ProductPriceDTO.builder()
                                .price("5000.00")
                                .currency("HKD")
                                .startDateTime("2023-05-01 00:00:00")
                                .endDateTime("2023-08-31 23:59:59")
                                .build()
                ))
                .status(Product.Status.ACTIVE.toString())
                .build();

        productService.saveProduct(productDTOToSave);
        assertThat(myProducts).hasSize(1);

        Product product = myProducts.get(0);
        assertThat(product.getPrices()).hasSize(1);

        ProductPrice productPrice = product.getPrices().get(0);
        assertThat(productPrice.getPrice().toString()).isEqualTo("5000.00");
        assertThat(productPrice.getCurrency()).isEqualTo(ProductPrice.Currency.HKD);
    }

    @Test
    public void testSaveProductChecksWork() {
        String productTypeCode = productDTOToSave.getProductTypeCode();
        productDTOToSave.setProductTypeCode(null);
        assertThatThrownBy(() -> {
            productService.saveProduct(productDTOToSave);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not provided");

        productDTOToSave.setProductTypeCode(productTypeCode);
        assertThatThrownBy(() -> {
            productService.saveProduct(productDTOToSave);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not exist");

    }
}
