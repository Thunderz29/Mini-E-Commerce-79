package com.e_commerce.inventory_service.service;

import java.util.List;

import com.e_commerce.inventory_service.dto.InventoryRequestDTO;
import com.e_commerce.inventory_service.dto.InventoryResponseDTO;
import com.e_commerce.inventory_service.dto.UpdateInventoryRequestDTO;

public interface InventoryService {
    // Create inventory
    InventoryResponseDTO createInventory(InventoryRequestDTO inventoryRequestDTO);

    // Get inventory by SKU code
    InventoryResponseDTO getInventoryBySkuCode(String skuCode);

    // Get all inventories
    List<InventoryResponseDTO> getAllInventory();

    // Update inventory using UpdateInventoryRequestDTO
    void updateInventory(Long id, UpdateInventoryRequestDTO updateInventoryRequestDTO);

    // Add inventory item
    void addInventoryItem(InventoryResponseDTO inventoryResponseDTO);

    // Delete inventory item
    void deleteInventoryItem(String skuCode);
}
