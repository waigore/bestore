package com.example.estore.service;

import com.example.estore.dto.ProductDiscountDTO;
import com.example.estore.entity.Product;
import com.example.estore.entity.ProductDiscount;
import com.example.estore.entity.ProductPrice;
import com.example.estore.entity.ProductType;
import com.example.estore.repository.ProductDiscountRepository;
import com.example.estore.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ProductDiscountServiceTests {
    private static final Logger LOG = LoggerFactory.getLogger(ProductDiscountServiceTests.class);
    @MockBean
    private ProductDiscountRepository productDiscountRepository;

    @MockBean
    private ProductRepository productRepository;

    @InjectMocks
    @Autowired
    private ProductDiscountService productDiscountService;

    private ProductType productType;

    private Product samsungTVProduct;

    private List<Product> products;

    @BeforeEach
    void setUp() {
        productType = ProductType.builder()
                .type("SMTV")
                .typeDisplayName("Smart TVs")
                .build();

        samsungTVProduct = Product.builder()
                .code("SAMSUNG_TV_01_BLACK")
                .productType(productType)
                .defaultDisplayName("Samsung Smart TV Black")
                .prices(List.of(
                        ProductPrice.builder()
                                .price(new BigDecimal("8000.00"))
                                .currency(ProductPrice.Currency.HKD)
                                .startDateTime(Timestamp.valueOf("2023-05-01 00:00:00"))
                                .endDateTime(Timestamp.valueOf("2023-08-31 23:59:59"))
                                .build()
                ))
                .discounts(List.of())
                .status(Product.Status.ACTIVE)
                .build();

        products = List.of(samsungTVProduct);

        given(productDiscountRepository.findAll()).willAnswer(i ->
                products.stream().map(product -> product.getDiscounts()).flatMap(Collection::stream).toList()
        );
        given(productDiscountRepository.findByCode(any())).will(i ->
                samsungTVProduct.getDiscounts().stream().filter(pd ->
                        pd.getCode().equals(i.getArgument(0))).findAny().orElse(null)
        );
        given(productDiscountRepository.save(any())).willAnswer(i -> {
            ProductDiscount pd = i.getArgument(0);
            samsungTVProduct.setDiscounts(List.of(pd));
            return pd;
        });
        doAnswer(i -> {
            samsungTVProduct.setDiscounts(List.of());
            return null;
        }).when(productDiscountRepository).delete(any());

        given(productRepository.findByCode(any())).willAnswer(i ->
                products.stream().filter(p -> p.getCode().equals(i.getArgument(0))).findAny().get()
        );
    }

    @Test
    void testFindAllDiscountsWorks() {
        ProductDiscount productDiscount = ProductDiscount.builder()
                .product(samsungTVProduct)
                .code("500OFFSAMSUNG")
                .lineDiscount(true)
                .description("$500 off Samsung TV purchase")
                .amount(new BigDecimal("500.00"))
                .appliedNumber(1)
                .appliedTimes(1)
                .startDateTime(Timestamp.valueOf("2023-05-01 00:00:00"))
                .endDateTime(Timestamp.valueOf("2023-05-31 23:59:59"))
                .build();
        samsungTVProduct.setDiscounts(List.of(productDiscount));

        List<ProductDiscountDTO> productDiscounts = productDiscountService.getAllDiscounts();
        assertThat(productDiscounts).hasSize(1);

        ProductDiscountDTO productDiscountDTO = productDiscounts.get(0);
        assertThat(productDiscountDTO.getProductCode()).isEqualTo(productDiscount.getProduct().getCode());
        assertThat(productDiscountDTO.getCode()).isEqualTo(productDiscount.getCode());
    }

    @Test
    void testCreateDiscountWorks() {
        ProductDiscountDTO productDiscountDTO = ProductDiscountDTO.builder()
                .productCode(samsungTVProduct.getCode())
                .code("500OFFSAMSUNG")
                .lineDiscount(true)
                .description("$500 off Samsung TV purchase")
                .amount("500.00")
                .appliedNumber(1)
                .appliedTimes(1)
                .startDateTime("2023-05-01 00:00:00")
                .endDateTime("2023-05-31 23:59:59")
                .build();

        productDiscountService.createDiscount(productDiscountDTO);
        LOG.info("product discount = " + samsungTVProduct.getDiscounts());

        ProductDiscountDTO foundDiscount = productDiscountService.findByCode("500OFFSAMSUNG");
        assertThat(foundDiscount).isNotNull();
        assertThat(foundDiscount.getProductCode()).isEqualTo(samsungTVProduct.getCode());
    }

    @Test
    void testDeleteDiscountWorks() {
        ProductDiscount productDiscount = ProductDiscount.builder()
                .product(samsungTVProduct)
                .code("500OFFSAMSUNG")
                .lineDiscount(true)
                .description("$500 off Samsung TV purchase")
                .amount(new BigDecimal("500.00"))
                .appliedNumber(1)
                .appliedTimes(1)
                .startDateTime(Timestamp.valueOf("2023-05-01 00:00:00"))
                .endDateTime(Timestamp.valueOf("2023-05-31 23:59:59"))
                .build();
        samsungTVProduct.setDiscounts(List.of(productDiscount));

        productDiscountService.deleteDiscount("500OFFSAMSUNG");
        assertThat(samsungTVProduct.getDiscounts()).hasSize(0);
    }
}
