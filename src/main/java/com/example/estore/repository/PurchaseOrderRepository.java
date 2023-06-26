package com.example.estore.repository;

import com.example.estore.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    public List<PurchaseOrder> findByCustomerIdentifier(String customerIdentifier);

    public PurchaseOrder findByOrderIdentifier(String orderIdentifier);
}
