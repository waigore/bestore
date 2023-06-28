package com.example.estore.controller;

import com.example.estore.dto.CreatePurchaseOrderDTO;
import com.example.estore.dto.PurchaseOrderDTO;
import com.example.estore.dto.UpdatePurchaseOrderDTO;
import com.example.estore.dto.api.APIResponseDTO;
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
    public ResponseEntity<APIResponseDTO<PurchaseOrderDTO>> findByOrderIdentifier(@PathVariable("identifier") String identifier) {
        try {
            return ResponseEntity.ok(
                    APIResponseDTO.<PurchaseOrderDTO>builder()
                            .body(purchaseOrderService.findByOrderIdentifier(identifier))
                            .build()
            );
        } catch (Exception e) {
            LOG.error("Exception finding order by identifier", e);
            return ResponseEntity.badRequest()
                    .body(APIResponseDTO.<PurchaseOrderDTO>builder()
                            .status(APIResponseDTO.Status.ERROR)
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public APIResponseDTO<PurchaseOrderDTO> createOrder(@RequestBody CreatePurchaseOrderDTO createPurchaseOrder) {
        return APIResponseDTO.<PurchaseOrderDTO>builder()
                .body(purchaseOrderService.createOrder(createPurchaseOrder))
                .build();
    }

    @PutMapping("{identifier}")
    public ResponseEntity<APIResponseDTO<PurchaseOrderDTO>> updateOrder(@PathVariable("identifier") String orderIdentifier,
                                                        @RequestBody UpdatePurchaseOrderDTO updatePurchaseOrder) {
        try {
            return ResponseEntity.ok(
                    APIResponseDTO.<PurchaseOrderDTO>builder()
                            .body(purchaseOrderService.updateOrder(orderIdentifier, updatePurchaseOrder))
                            .build()
            );
        } catch (Exception e) {
            LOG.error("Exception updating order", e);
            return ResponseEntity.badRequest()
                    .body(APIResponseDTO.<PurchaseOrderDTO>builder()
                            .status(APIResponseDTO.Status.ERROR)
                            .errorMessage(e.getMessage())
                            .build());
        }
    }
}
