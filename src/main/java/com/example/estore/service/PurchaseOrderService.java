package com.example.estore.service;

import com.example.estore.dto.CreatePurchaseOrderDTO;
import com.example.estore.dto.PurchaseOrderBasketItemDTO;
import com.example.estore.dto.PurchaseOrderDTO;
import com.example.estore.entity.*;
import com.example.estore.mapper.PurchaseOrderMapper;
import com.example.estore.repository.ProductRepository;
import com.example.estore.repository.PurchaseOrderRepository;
import com.example.estore.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseOrderService {
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private IdUtil idUtil;

    public PurchaseOrderDTO findByOrderIdentifier(String orderIdentifier) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByOrderIdentifier(orderIdentifier);
        if (purchaseOrder == null) {
            throw new IllegalArgumentException("No such order identifier: '" + orderIdentifier + "'");
        }

        return purchaseOrderMapper.entityToDto(purchaseOrder);
    }

    public PurchaseOrderDTO createOrder(CreatePurchaseOrderDTO createPurchaseOrder) {
        if (createPurchaseOrder.getCustomerIdentifier() == null) {
            throw new IllegalArgumentException("No customer identifier provided");
        }
        if (createPurchaseOrder.getOrderItems() == null || createPurchaseOrder.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order to create must have at least one item");
        }
        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .customerIdentifier(createPurchaseOrder.getCustomerIdentifier())
                .orderIdentifier(idUtil.generateNewIdentifier().toString())
                .orderBasket(PurchaseOrderBasket.builder().build())
                .orderReceipt(PurchaseOrderReceipt.builder().build())
                .status(PurchaseOrder.Status.CREATED)
                .createdTime(Timestamp.from(Instant.now()))
                .build();

        List<PurchaseOrderBasketItem> basketItems = new ArrayList();
        List<PurchaseOrderReceiptItem> receiptItems = new ArrayList();
        createPurchaseOrder.getOrderItems().forEach(item -> {
            Product product = productRepository.findByCodeAndStatus(item.getProductCode(), Product.Status.ACTIVE);
            if (product == null) {
                throw new IllegalArgumentException("No active product with code '" + item.getProductCode() + "'");
            }
            ProductPrice productPrice = product.getPrices().stream().filter(price ->
                    (price.getEndDateTime() == null || Timestamp.from(Instant.now()).before(price.getEndDateTime())) &&
                            price.getStartDateTime() == null || Timestamp.from(Instant.now()).after(price.getStartDateTime()))
                    .findFirst().orElse(null);

            PurchaseOrderBasketItem purchaseOrderBasketItem = PurchaseOrderBasketItem.builder()
                    .orderBasket(purchaseOrder.getOrderBasket())
                    .itemReference(idUtil.generateNewIdentifier().toString())
                    .productCode(item.getProductCode())
                    .quantity(item.getQuantity())
                    .build();
            basketItems.add(purchaseOrderBasketItem);

            BigDecimal price = productPrice != null ? productPrice.getPrice() : new BigDecimal(0);
            PurchaseOrderReceiptItem purchaseOrderReceiptItem = PurchaseOrderReceiptItem.builder()
                    .orderReceipt(purchaseOrder.getOrderReceipt())
                    .orderBasketItemReference(purchaseOrderBasketItem.getItemReference())
                    .itemPrice(price.multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();
            receiptItems.add(purchaseOrderReceiptItem);
        });
        BigDecimal totalPrice = receiptItems.stream()
                .map(item -> item.getItemPrice())
                .reduce(new BigDecimal(0), (subtotal, price) -> subtotal.add(price));
        purchaseOrder.getOrderReceipt().setTotalPrice(totalPrice);

        purchaseOrder.getOrderBasket().setOrder(purchaseOrder);
        purchaseOrder.getOrderBasket().setItems(basketItems);

        purchaseOrder.getOrderReceipt().setOrder(purchaseOrder);
        purchaseOrder.getOrderReceipt().setItems(receiptItems);

        PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.entityToDto(savedPurchaseOrder);
    }
}
