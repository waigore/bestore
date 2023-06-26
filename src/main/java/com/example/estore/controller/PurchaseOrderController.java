package com.example.estore.controller;

import com.example.estore.dto.CreatePurchaseOrderDTO;
import com.example.estore.dto.PurchaseOrderDTO;
import com.example.estore.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping("{identifier}")
    public ResponseEntity<PurchaseOrderDTO> findByOrderIdentifier(@PathVariable("identifier") String identifier) {
        try {
            return ResponseEntity.ok(purchaseOrderService.findByOrderIdentifier(identifier));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrderDTO createOrder(@RequestBody CreatePurchaseOrderDTO createPurchaseOrder) {
        return purchaseOrderService.createOrder(createPurchaseOrder);
    }
}
