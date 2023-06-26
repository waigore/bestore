package com.example.estore.mapper;

import com.example.estore.dto.*;
import com.example.estore.entity.*;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface PurchaseOrderMapper {
    PurchaseOrderDTO entityToDto(PurchaseOrder purchaseOrder);

    PurchaseOrderBasketDTO entityToDto(PurchaseOrderBasket purchaseOrderBasket);

    PurchaseOrderBasketItemDTO entityToDto(PurchaseOrderBasketItem purchaseOrderBasketItem);

    PurchaseOrderReceiptDTO entityToDto(PurchaseOrderReceipt purchaseReceipt);

    PurchaseOrderReceiptItemDTO entityToDto(PurchaseOrderReceiptItem purchaseOrderReceiptItem);
}
