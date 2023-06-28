package com.example.estore.service;

import com.example.estore.dto.*;
import com.example.estore.entity.*;
import com.example.estore.repository.ProductRepository;
import com.example.estore.repository.PurchaseOrderRepository;
import com.example.estore.util.IdUtil;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class PurchaseOrderServiceTests {
    @MockBean
    private PurchaseOrderRepository purchaseOrderRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private IdUtil idUtil;

    @InjectMocks
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    private Product lgTVProduct;

    private Product samsungTVProduct;

    private ProductType productType;

    private List<Product> products;

    private List<PurchaseOrder> purchaseOrders = new ArrayList();

    private CreatePurchaseOrderDTO createPurchaseOrder;

    @BeforeEach
    void setUp() {
        productType = ProductType.builder()
                .type("SMTV")
                .typeDisplayName("Smart TVs")
                .build();

        lgTVProduct = Product.builder()
                .code("LG_TV_01_GREEN")
                .productType(productType)
                .defaultDisplayName("LG Smart TV Green")
                .prices(List.of(
                        ProductPrice.builder()
                                .price(new BigDecimal("5000.00"))
                                .currency(ProductPrice.Currency.HKD)
                                .startDateTime(Timestamp.valueOf("2023-05-01 00:00:00"))
                                .endDateTime(Timestamp.valueOf("2023-08-31 23:59:59"))
                                .build()
                ))
                .status(Product.Status.ACTIVE)
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
                .status(Product.Status.ACTIVE)
                .build();

        products = List.of(lgTVProduct, samsungTVProduct);

        createPurchaseOrder = CreatePurchaseOrderDTO.builder()
                .orderItems(List.of(
                        CreatePurchaseOrderItemDTO.builder()
                                .productCode("LG_TV_01_GREEN")
                                .quantity(1)
                                .build()
                ))
                .customerIdentifier("790d0828-46c3-4a0a-a7b9-94e20895261e")
                .build();

        purchaseOrders.clear();

        given(idUtil.generateNewIdentifier()).willReturn(UUID.fromString("36a26584-c4e5-4940-b327-3fbf837c51eb"));

        given(productRepository.findByCodeAndStatus(any(), any())).willAnswer(i ->
                products.stream().filter(p ->
                        p.getCode().equals(i.getArgument(0)) &&
                                p.getStatus().equals(i.getArgument(1))
                ).findAny().get()
        );

        given(purchaseOrderRepository.save(any())).willAnswer(i -> {
            PurchaseOrder purchaseOrder = i.getArgument(0);
            if (purchaseOrder.getId() == null) {
                purchaseOrder.setId((long)(purchaseOrders.size()+1));
            }
            purchaseOrders.add(purchaseOrder);
            return purchaseOrder;
        });
        given(purchaseOrderRepository.findByOrderIdentifier(any())).willAnswer(i ->
                purchaseOrders.stream().filter(o -> o.getOrderIdentifier().equals(i.getArgument(0))).findAny().get()
        );
    }

    @Test
    public void testFindOrderByIdentifierWorks() {
        PurchaseOrder order = PurchaseOrder.builder()
                .id(1L)
                .orderIdentifier("e2703b64-1d11-494f-8978-fadbed5cc47d")
                .customerIdentifier("790d0828-46c3-4a0a-a7b9-94e20895261e")
                .createdTime(Timestamp.from(Instant.now()))
                .status(PurchaseOrder.Status.CREATED)
                .orderBasket(PurchaseOrderBasket.builder()
                        .items(List.of(
                                PurchaseOrderBasketItem.builder()
                                        .productCode("SAMSUNG_TV_01_BLACK")
                                        .itemReference("e2703b64-1d11-494f-8978-fadbed5cc47d_1")
                                        .quantity(1)
                                        .build()
                        ))
                        .build()
                )
                .orderReceipt(PurchaseOrderReceipt.builder()
                        .totalPrice(new BigDecimal("10000.00"))
                        .items(List.of(
                                PurchaseOrderReceiptItem.builder()
                                        .orderBasketItemReference("e2703b64-1d11-494f-8978-fadbed5cc47d_1")
                                        .itemPrice(new BigDecimal("10000.00"))
                                        .build()
                        ))
                        .build())
                .build();
        purchaseOrders.add(order);

        PurchaseOrderDTO myOrderDTO = purchaseOrderService.findByOrderIdentifier("e2703b64-1d11-494f-8978-fadbed5cc47d");
        assertThat(myOrderDTO.getOrderIdentifier()).isEqualTo(order.getOrderIdentifier());
        assertThat(myOrderDTO.getCustomerIdentifier()).isEqualTo(order.getCustomerIdentifier());
        assertThat(myOrderDTO.getStatus()).isEqualTo(order.getStatus().toString());
        assertThat(myOrderDTO.getOrderBasket().getItems()).hasSameSizeAs(order.getOrderBasket().getItems());
        assertThat(myOrderDTO.getOrderReceipt().getItems()).hasSameSizeAs(order.getOrderReceipt().getItems());
    }

    @Test
    public void testCreateOrderWorks() {
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderService.createOrder(createPurchaseOrder);

        assertThat(purchaseOrderDTO.getId()).isEqualTo(1);
        assertThat(purchaseOrderDTO.getOrderIdentifier()).isEqualTo("36a26584-c4e5-4940-b327-3fbf837c51eb");
        assertThat(purchaseOrderDTO.getOrderBasket()).isNotNull();
        assertThat(purchaseOrderDTO.getOrderBasket().getItems()).hasSize(1);
        assertThat(purchaseOrderDTO.getOrderReceipt()).isNotNull();
        assertThat(purchaseOrderDTO.getOrderReceipt().getItems()).hasSize(1);

        PurchaseOrderBasketItemDTO basketItemDTO = purchaseOrderDTO.getOrderBasket().getItems().get(0);
        assertThat(basketItemDTO.getProductCode()).isEqualTo("LG_TV_01_GREEN");
        assertThat(basketItemDTO.getItemReference()).isEqualTo("36a26584-c4e5-4940-b327-3fbf837c51eb");
        assertThat(basketItemDTO.getQuantity()).isEqualTo(1);

        PurchaseOrderReceiptItemDTO receiptItemDTO = purchaseOrderDTO.getOrderReceipt().getItems().get(0);
        assertThat(receiptItemDTO.getItemPrice()).isEqualTo("5000.00");
        assertThat(receiptItemDTO.getOrderBasketItemReference()).isEqualTo(basketItemDTO.getItemReference());
        assertThat(receiptItemDTO.getDiscount()).isNull();
    }

    @Test
    public void testUpdateOrderWorks() {
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderService.createOrder(createPurchaseOrder);
        String itemReference = purchaseOrderDTO.getOrderBasket().getItems().get(0).getItemReference();

        List<String> uuids = Lists.newArrayList(
                "908ed369-c3c8-403c-9cbe-bb37a7c6b186",
                "5e20970d-ad07-4971-bf89-e84fc39b7c1a",
                "e4c4e773-dba3-4d59-84db-304a4b9f9cf0",
                "ee0244e9-8210-435f-aa78-c94eeeea1fa1",
                "9a7d228d-0644-4491-88e2-41f8e8a5a074"
        );
        given(idUtil.generateNewIdentifier()).willAnswer(i ->
                !uuids.isEmpty() ? UUID.fromString(uuids.remove(0)) : UUID.randomUUID().toString()
        );

        ProductDiscount productDiscount = ProductDiscount.builder()
                .product(samsungTVProduct)
                .code("500OFFSAMSUNG")
                .lineDiscount(true)
                .description("$500 off Samsung TV purchase")
                .amount(new BigDecimal("500.00"))
                .appliedNumber(1)
                .appliedTimes(1)
                .startDateTime(Timestamp.valueOf("2023-05-01 00:00:00"))
                .endDateTime(Timestamp.valueOf("2023-08-31 23:59:59"))
                .build();
        samsungTVProduct.setDiscounts(List.of(productDiscount));
        UpdatePurchaseOrderDTO updatePurchaseOrder = UpdatePurchaseOrderDTO.builder()
                .addedItems(List.of(
                        CreatePurchaseOrderItemDTO.builder()
                                .productCode("SAMSUNG_TV_01_BLACK")
                                .quantity(3)
                                .build()
                ))
                .deletedItems(List.of(
                        DeletePurchaseOrderItemDTO.builder()
                                .itemReference(itemReference)
                                .build()
                ))
                .build();

        PurchaseOrderDTO updatedOrderDTO = purchaseOrderService.updateOrder(purchaseOrderDTO.getOrderIdentifier(), updatePurchaseOrder);

        assertThat(updatedOrderDTO.getOrderIdentifier()).isEqualTo("36a26584-c4e5-4940-b327-3fbf837c51eb");
        assertThat(updatedOrderDTO.getOrderBasket().getItems()).hasSize(1);
        assertThat(updatedOrderDTO.getOrderReceipt().getItems()).hasSize(1);

        PurchaseOrderBasketItemDTO basketItemDTO = updatedOrderDTO.getOrderBasket().getItems().get(0);
        assertThat(basketItemDTO.getProductCode()).isEqualTo("SAMSUNG_TV_01_BLACK");
        assertThat(basketItemDTO.getQuantity()).isEqualTo(3);

        PurchaseOrderReceiptItemDTO receiptItemDTO = updatedOrderDTO.getOrderReceipt().getItems().get(0);
        assertThat(receiptItemDTO.getItemPrice()).isEqualTo("22500.00");
        assertThat(receiptItemDTO.getOrderBasketItemReference()).isEqualTo(basketItemDTO.getItemReference());

        assertThat(receiptItemDTO.getDiscount()).isNotNull();
        PurchaseOrderReceiptItemDiscountDTO itemDiscount = receiptItemDTO.getDiscount();
        assertThat(itemDiscount.getDiscountedAmount()).isEqualTo("1500.00");
        assertThat(itemDiscount.getProductDiscountCode()).isEqualTo("500OFFSAMSUNG");

        assertThat(updatedOrderDTO.getOrderReceipt().getTotalPrice()).isEqualTo("22500.00");
    }

    @Test
    public void testCreateOrderWithLineDiscountWorks() {
        ProductDiscount productDiscount = ProductDiscount.builder()
                .product(lgTVProduct)
                .code("50OFF")
                .lineDiscount(true)
                .description("50% off TV purchase")
                .percentage(50)
                .appliedNumber(1)
                .appliedTimes(1)
                .startDateTime(Timestamp.valueOf("2023-05-01 00:00:00"))
                .endDateTime(Timestamp.valueOf("2023-08-31 23:59:59"))
                .build();
        lgTVProduct.setDiscounts(List.of(productDiscount));

        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderService.createOrder(createPurchaseOrder);

        PurchaseOrderBasketItemDTO basketItemDTO = purchaseOrderDTO.getOrderBasket().getItems().get(0);
        assertThat(basketItemDTO.getProductCode()).isEqualTo("LG_TV_01_GREEN");
        assertThat(basketItemDTO.getItemReference()).isEqualTo("36a26584-c4e5-4940-b327-3fbf837c51eb");
        assertThat(basketItemDTO.getQuantity()).isEqualTo(1);

        PurchaseOrderReceiptItemDTO receiptItemDTO = purchaseOrderDTO.getOrderReceipt().getItems().get(0);
        assertThat(receiptItemDTO.getItemPrice()).isEqualTo("2500.00");
        assertThat(receiptItemDTO.getOrderBasketItemReference()).isEqualTo(basketItemDTO.getItemReference());
        assertThat(receiptItemDTO.getDiscount()).isNotNull();

        PurchaseOrderReceiptItemDiscountDTO itemDiscountDTO = receiptItemDTO.getDiscount();
        assertThat(itemDiscountDTO.getProductDiscountCode()).isEqualTo("50OFF");
        assertThat(itemDiscountDTO.getDiscountedAmount()).isEqualTo("2500.00");
    }

    @Test
    public void testCreateOrderWithExpiredDiscountsWorks() {
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

        List<CreatePurchaseOrderItemDTO> createPurchaseOrderItems = List.of(
                CreatePurchaseOrderItemDTO.builder()
                        .productCode("SAMSUNG_TV_01_BLACK")
                        .quantity(3)
                        .build()
        );
        createPurchaseOrder.setOrderItems(createPurchaseOrderItems);

        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderService.createOrder(createPurchaseOrder);

        PurchaseOrderBasketItemDTO basketItemDTO = purchaseOrderDTO.getOrderBasket().getItems().get(0);
        assertThat(basketItemDTO.getProductCode()).isEqualTo("SAMSUNG_TV_01_BLACK");
        assertThat(basketItemDTO.getItemReference()).isEqualTo("36a26584-c4e5-4940-b327-3fbf837c51eb");
        assertThat(basketItemDTO.getQuantity()).isEqualTo(3);

        PurchaseOrderReceiptItemDTO receiptItemDTO = purchaseOrderDTO.getOrderReceipt().getItems().get(0);
        assertThat(receiptItemDTO.getItemPrice()).isEqualTo("24000.00");
        assertThat(receiptItemDTO.getOrderBasketItemReference()).isEqualTo(basketItemDTO.getItemReference());
        assertThat(receiptItemDTO.getDiscount()).isNull();

    }

    @Test
    public void testCreateOrderWithMultipleDiscountsWorks() {
        ProductDiscount productDiscount = ProductDiscount.builder()
                .product(samsungTVProduct)
                .code("500OFFSAMSUNG")
                .lineDiscount(true)
                .description("$500 off Samsung TV purchase")
                .amount(new BigDecimal("500.00"))
                .appliedNumber(1)
                .appliedTimes(1)
                .startDateTime(Timestamp.valueOf("2023-05-01 00:00:00"))
                .endDateTime(Timestamp.valueOf("2023-08-31 23:59:59"))
                .build();
        ProductDiscount productDiscount2 = ProductDiscount.builder()
                .product(lgTVProduct)
                .code("50OFFLG")
                .lineDiscount(false)
                .description("50% off TV purchase")
                .amount(new BigDecimal("500.00"))
                .appliedNumber(3)
                .appliedTimes(2)
                .startDateTime(Timestamp.valueOf("2023-05-01 00:00:00"))
                .endDateTime(Timestamp.valueOf("2023-08-31 23:59:59"))
                .build();
        samsungTVProduct.setDiscounts(List.of(productDiscount));
        lgTVProduct.setDiscounts(List.of(productDiscount2));

        List<CreatePurchaseOrderItemDTO> createPurchaseOrderItems = List.of(
                CreatePurchaseOrderItemDTO.builder()
                        .productCode("SAMSUNG_TV_01_BLACK")
                        .quantity(1)
                        .build(),
                CreatePurchaseOrderItemDTO.builder()
                        .productCode("LG_TV_01_GREEN")
                        .quantity(3)
                        .build()
        );
        createPurchaseOrder.setOrderItems(createPurchaseOrderItems);

        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderService.createOrder(createPurchaseOrder);
        assertThat(purchaseOrderDTO.getOrderBasket().getItems()).hasSize(2);
        assertThat(purchaseOrderDTO.getOrderReceipt().getItems()).hasSize(2);
        assertThat(purchaseOrderDTO.getOrderReceipt().getTotalPrice()).isEqualTo("21500.00");

        //Samsung TV
        PurchaseOrderBasketItemDTO basketItemDTO = purchaseOrderDTO.getOrderBasket().getItems().get(0);
        assertThat(basketItemDTO.getProductCode()).isEqualTo("SAMSUNG_TV_01_BLACK");
        assertThat(basketItemDTO.getQuantity()).isEqualTo(1);

        PurchaseOrderReceiptItemDTO receiptItemDTO = purchaseOrderDTO.getOrderReceipt().getItems().get(0);
        assertThat(receiptItemDTO.getItemPrice()).isEqualTo("7500.00");
        assertThat(receiptItemDTO.getOrderBasketItemReference()).isEqualTo(basketItemDTO.getItemReference());
        assertThat(receiptItemDTO.getDiscount()).isNotNull();

        PurchaseOrderReceiptItemDiscountDTO itemDiscountDTO = receiptItemDTO.getDiscount();
        assertThat(itemDiscountDTO.getProductDiscountCode()).isEqualTo("500OFFSAMSUNG");
        assertThat(itemDiscountDTO.getDiscountedAmount()).isEqualTo("500.00");

        //LG TV
        PurchaseOrderBasketItemDTO basketItemDTO2 = purchaseOrderDTO.getOrderBasket().getItems().get(1);
        assertThat(basketItemDTO2.getProductCode()).isEqualTo("LG_TV_01_GREEN");
        assertThat(basketItemDTO2.getQuantity()).isEqualTo(3);

        PurchaseOrderReceiptItemDTO receiptItemDTO2 = purchaseOrderDTO.getOrderReceipt().getItems().get(1);
        assertThat(receiptItemDTO2.getItemPrice()).isEqualTo("14000.00");
        assertThat(receiptItemDTO2.getOrderBasketItemReference()).isEqualTo(basketItemDTO2.getItemReference());
        assertThat(receiptItemDTO2.getDiscount()).isNotNull();

        PurchaseOrderReceiptItemDiscountDTO itemDiscountDTO2 = receiptItemDTO2.getDiscount();
        assertThat(itemDiscountDTO2.getProductDiscountCode()).isEqualTo("50OFFLG");
        assertThat(itemDiscountDTO2.getDiscountedAmount()).isEqualTo("1000.00");
    }

    @Test
    public void testCreateOrderWithItemDiscountWorks() {
        ProductDiscount productDiscount = ProductDiscount.builder()
                .product(lgTVProduct)
                .code("50OFF2ND")
                .lineDiscount(false)
                .description("50% off 2nd TV purchase")
                .percentage(50)
                .appliedNumber(2)
                .appliedTimes(1)
                .startDateTime(Timestamp.valueOf("2023-05-01 00:00:00"))
                .endDateTime(Timestamp.valueOf("2023-08-31 23:59:59"))
                .build();
        lgTVProduct.setDiscounts(List.of(productDiscount));

        List<CreatePurchaseOrderItemDTO> createPurchaseOrderItems = List.of(
                CreatePurchaseOrderItemDTO.builder()
                        .productCode("LG_TV_01_GREEN")
                        .quantity(3)
                        .build()
        );
        createPurchaseOrder.setOrderItems(createPurchaseOrderItems);

        PurchaseOrderDTO purchaseOrderDTO = purchaseOrderService.createOrder(createPurchaseOrder);

        PurchaseOrderBasketItemDTO basketItemDTO = purchaseOrderDTO.getOrderBasket().getItems().get(0);
        assertThat(basketItemDTO.getProductCode()).isEqualTo("LG_TV_01_GREEN");
        assertThat(basketItemDTO.getItemReference()).isEqualTo("36a26584-c4e5-4940-b327-3fbf837c51eb");
        assertThat(basketItemDTO.getQuantity()).isEqualTo(3);

        PurchaseOrderReceiptItemDTO receiptItemDTO = purchaseOrderDTO.getOrderReceipt().getItems().get(0);
        assertThat(receiptItemDTO.getItemPrice()).isEqualTo("12500.00");
        assertThat(receiptItemDTO.getOrderBasketItemReference()).isEqualTo(basketItemDTO.getItemReference());
        assertThat(receiptItemDTO.getDiscount()).isNotNull();

        PurchaseOrderReceiptItemDiscountDTO itemDiscountDTO = receiptItemDTO.getDiscount();
        assertThat(itemDiscountDTO.getProductDiscountCode()).isEqualTo("50OFF2ND");
        assertThat(itemDiscountDTO.getDiscountedAmount()).isEqualTo("2500.00");
    }


}
