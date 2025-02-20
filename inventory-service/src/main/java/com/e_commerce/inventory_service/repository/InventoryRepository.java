package com.e_commerce.inventory_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_commerce.inventory_service.model.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Query method to find inventory by SKU code
    Optional<Inventory> findBySkuCode(String skuCode);

    // Check if SKU code exists
    boolean existsBySkuCode(String skuCode);
}
