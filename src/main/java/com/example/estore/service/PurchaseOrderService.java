package com.example.estore.service;

import com.example.estore.dto.CreatePurchaseOrderDTO;
import com.example.estore.dto.CreatePurchaseOrderItemDTO;
import com.example.estore.dto.PurchaseOrderDTO;
import com.example.estore.dto.UpdatePurchaseOrderDTO;
import com.example.estore.entity.*;
import com.example.estore.exception.NoSuchObjectException;
import com.example.estore.mapper.PurchaseOrderMapper;
import com.example.estore.repository.ProductRepository;
import com.example.estore.repository.PurchaseOrderRepository;
import com.example.estore.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
            throw new NoSuchObjectException("No such order identifier: '" + orderIdentifier + "'");
        }

        return purchaseOrderMapper.entityToDto(purchaseOrder);
    }

    private void updateOrderReceiptPrice(PurchaseOrder purchaseOrder) {
        List<PurchaseOrderReceiptItem> receiptItems = purchaseOrder.getOrderReceipt().getItems();
        BigDecimal totalPrice = receiptItems.stream()
                .map(item -> item.getItemPrice())
                .reduce(new BigDecimal(0), (subtotal, price) -> subtotal.add(price));
        purchaseOrder.getOrderReceipt().setTotalPrice(totalPrice);
    }

    private PurchaseOrderBasketItem newOrderBasketItem(CreatePurchaseOrderItemDTO itemDTO, PurchaseOrderBasket basket) {
        return PurchaseOrderBasketItem.builder()
                .orderBasket(basket)
                .itemReference(idUtil.generateNewIdentifier().toString())
                .productCode(itemDTO.getProductCode())
                .quantity(itemDTO.getQuantity())
                .build();
    }

    private PurchaseOrderReceiptItem newPurchaseOrderReceiptItem(
            PurchaseOrderReceipt purchaseOrderReceipt,
            PurchaseOrderBasketItem purchaseOrderBasketItem,
            Product product) {
        ProductPrice productPrice = product.getPrices().stream().filter(price ->
                        Timestamp.from(Instant.now()).before(price.getEndDateTime()) &&
                                Timestamp.from(Instant.now()).after(price.getStartDateTime()))
                .findFirst().orElse(null);
        BigDecimal price = productPrice != null ? productPrice.getPrice() : new BigDecimal(0);
        PurchaseOrderReceiptItem purchaseOrderReceiptItem = PurchaseOrderReceiptItem.builder()
                .orderReceipt(purchaseOrderReceipt)
                .orderBasketItemReference(purchaseOrderBasketItem.getItemReference())
                .itemPrice(price.multiply(BigDecimal.valueOf(purchaseOrderBasketItem.getQuantity())))
                .build();

        calculateDiscountForItem(product, productPrice, purchaseOrderBasketItem, purchaseOrderReceiptItem);

        return purchaseOrderReceiptItem;
    }

    private void calculateDiscountForItem(
            Product product,
            ProductPrice productPrice,
            PurchaseOrderBasketItem basketItem,
            PurchaseOrderReceiptItem receiptItem) {
        if (CollectionUtils.isEmpty(product.getDiscounts())) {
            return;
        }

        BigDecimal discountedAmount = null;
        PurchaseOrderReceiptItemDiscount itemDiscount = null;
        BigDecimal price = productPrice.getPrice();
        Timestamp now = Timestamp.from(Instant.now());

        ProductDiscount productDiscount = product.getDiscounts().stream().filter(discount -> {
            if (now.after(discount.getEndDateTime()) || now.before(discount.getStartDateTime()))
                return false;
            if (discount.getAppliedNumber() > basketItem.getQuantity())
                return false;
            return true;
        }).findFirst().orElse(null);
        if (productDiscount == null) {
            return;
        }

        Integer percentage = productDiscount.getPercentage();
        BigDecimal flatDiscountAmount = productDiscount.getAmount();
        int multiplier;

        if (productDiscount.getLineDiscount()) {
            multiplier = basketItem.getQuantity();
        }
        else {
            multiplier = basketItem.getQuantity()
                    / productDiscount.getAppliedNumber()
                    * productDiscount.getAppliedTimes();
        }

        if (percentage != null && percentage > 0) {
            discountedAmount = price
                    .multiply(BigDecimal.valueOf(percentage))
                    .divide(BigDecimal.valueOf(100))
                    .multiply(BigDecimal.valueOf(multiplier));
        }
        else {
            discountedAmount = flatDiscountAmount
                    .multiply(BigDecimal.valueOf(multiplier));
        }

        if (discountedAmount.equals(BigDecimal.ZERO)) {
            return;
        }

        if (receiptItem.getItemPrice().compareTo(discountedAmount) < 0) {
            discountedAmount = receiptItem.getItemPrice();
        }
        itemDiscount = PurchaseOrderReceiptItemDiscount.builder()
                .orderReceiptItem(receiptItem)
                .productDiscountCode(productDiscount.getCode())
                .discountedAmount(discountedAmount)
                .build();
        receiptItem.setItemPrice(receiptItem.getItemPrice().subtract(discountedAmount));
        receiptItem.setDiscount(itemDiscount);
    }

    @Transactional
    public PurchaseOrderDTO createOrder(CreatePurchaseOrderDTO createPurchaseOrder) {
        if (createPurchaseOrder.getCustomerIdentifier() == null) {
            throw new IllegalArgumentException("No customer identifier provided");
        }
        if (CollectionUtils.isEmpty(createPurchaseOrder.getOrderItems())) {
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
                throw new NoSuchObjectException("No active product with code '" + item.getProductCode() + "'");
            }

            PurchaseOrderBasketItem purchaseOrderBasketItem = newOrderBasketItem(item, purchaseOrder.getOrderBasket());
            basketItems.add(purchaseOrderBasketItem);

            PurchaseOrderReceiptItem purchaseOrderReceiptItem = newPurchaseOrderReceiptItem(
                    purchaseOrder.getOrderReceipt(), purchaseOrderBasketItem, product);
            receiptItems.add(purchaseOrderReceiptItem);
        });
        purchaseOrder.getOrderBasket().setOrder(purchaseOrder);
        purchaseOrder.getOrderBasket().setItems(basketItems);

        purchaseOrder.getOrderReceipt().setOrder(purchaseOrder);
        purchaseOrder.getOrderReceipt().setItems(receiptItems);

        updateOrderReceiptPrice(purchaseOrder);

        PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.entityToDto(savedPurchaseOrder);
    }

    @Transactional
    public PurchaseOrderDTO updateOrder(String orderIdentifier, UpdatePurchaseOrderDTO updatePurchaseOrder) {
        if (orderIdentifier == null || orderIdentifier.isBlank()) {
            throw new IllegalArgumentException("Order identifier must be provided");
        }
        if (CollectionUtils.isEmpty(updatePurchaseOrder.getAddedItems()) &&
               CollectionUtils.isEmpty(updatePurchaseOrder.getDeletedItems())) {
            throw new IllegalArgumentException("At least one added or deleted item must be provided");
        }

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByOrderIdentifier(orderIdentifier);
        if (purchaseOrder == null) {
            throw new NoSuchObjectException("No order with identifier '" + orderIdentifier + "' found");
        }

        if (!CollectionUtils.isEmpty(updatePurchaseOrder.getDeletedItems())) {
            updatePurchaseOrder.getDeletedItems().forEach(deletedItem -> {
                PurchaseOrderBasketItem basketItem = purchaseOrder.getOrderBasket().getItems().stream().filter(bi ->
                        bi.getItemReference().equals(deletedItem.getItemReference())).findFirst().orElse(null);
                if (basketItem == null) {
                    throw new NoSuchObjectException("No such basket item with reference '" + deletedItem.getItemReference() + "'");
                }
                PurchaseOrderReceiptItem receiptItem = purchaseOrder.getOrderReceipt().getItems().stream().filter(ri ->
                        ri.getOrderBasketItemReference().equals(deletedItem.getItemReference())).findFirst().orElse(null);
                if (receiptItem == null) {
                    throw new NoSuchObjectException("No such receipt item with reference '" + deletedItem.getItemReference() + "'");
                }

                purchaseOrder.getOrderBasket().getItems().remove(basketItem);
                purchaseOrder.getOrderReceipt().getItems().remove(receiptItem);
            });
        }

        if (!CollectionUtils.isEmpty(updatePurchaseOrder.getAddedItems())) {
            updatePurchaseOrder.getAddedItems().forEach(addedItem -> {
                Product product = productRepository.findByCodeAndStatus(addedItem.getProductCode(), Product.Status.ACTIVE);
                if (product == null) {
                    throw new NoSuchObjectException("No active product with code '" + addedItem.getProductCode() + "'");
                }

                PurchaseOrderBasketItem purchaseOrderBasketItem = newOrderBasketItem(addedItem, purchaseOrder.getOrderBasket());
                purchaseOrder.getOrderBasket().getItems().add(purchaseOrderBasketItem);

                PurchaseOrderReceiptItem purchaseOrderReceiptItem = newPurchaseOrderReceiptItem(
                        purchaseOrder.getOrderReceipt(), purchaseOrderBasketItem, product);
                purchaseOrder.getOrderReceipt().getItems().add(purchaseOrderReceiptItem);
            });
        }

        if (purchaseOrder.getOrderBasket().getItems().isEmpty()) {
            throw new IllegalStateException("Basket is empty after update");
        }

        updateOrderReceiptPrice(purchaseOrder);

        PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.entityToDto(savedPurchaseOrder);
    }
}
