package com.example.estore.controller;

import com.example.estore.dto.CreatePurchaseOrderDTO;
import com.example.estore.dto.PurchaseOrderDTO;
import com.example.estore.dto.UpdatePurchaseOrderDTO;
import com.example.estore.service.PurchaseOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class PurchaseOrderController {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderController.class);

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping("{identifier}")
    public ResponseEntity<PurchaseOrderDTO> findByOrderIdentifier(@PathVariable("identifier") String identifier) {
        try {
            return ResponseEntity.ok(purchaseOrderService.findByOrderIdentifier(identifier));
        } catch (IllegalArgumentException | IllegalStateException e) {
            LOG.error("Exception finding order by identifier", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PurchaseOrderDTO> createOrder(@RequestBody CreatePurchaseOrderDTO createPurchaseOrder) {
        try {
            return ResponseEntity.ok(purchaseOrderService.createOrder(createPurchaseOrder));
        } catch (IllegalArgumentException | IllegalStateException e) {
            LOG.error("Exception creating order", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("{identifier}")
    public ResponseEntity<PurchaseOrderDTO> updateOrder(@PathVariable("identifier") String orderIdentifier,
                                                        @RequestBody UpdatePurchaseOrderDTO updatePurchaseOrder) {
        try {
            return ResponseEntity.ok(purchaseOrderService.updateOrder(orderIdentifier, updatePurchaseOrder));
        } catch (IllegalArgumentException | IllegalStateException e) {
            LOG.error("Exception updating order", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
