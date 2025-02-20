package com.e_commerce.inventory_service.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce.inventory_service.dto.InventoryRequestDTO;
import com.e_commerce.inventory_service.dto.InventoryResponseDTO;
import com.e_commerce.inventory_service.dto.UpdateInventoryRequestDTO;
import com.e_commerce.inventory_service.exception.InventoryNotFoundException;
import com.e_commerce.inventory_service.model.Inventory;
import com.e_commerce.inventory_service.repository.InventoryRepository;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    // Create Inventory
    @Override
    public InventoryResponseDTO createInventory(InventoryRequestDTO inventoryRequestDTO) {
        // Cek apakah SKU Code sudah ada
        if (inventoryRepository.existsBySkuCode(inventoryRequestDTO.getSkuCode())) {
            throw new IllegalArgumentException(
                    "Inventory with SKU Code already exists: " + inventoryRequestDTO.getSkuCode());
        }

        // Map DTO to entity
        Inventory inventory = new Inventory();
        inventory.setSkuCode(inventoryRequestDTO.getSkuCode());
        inventory.setQuantity(inventoryRequestDTO.getQuantity());
        inventory.setPrice(inventoryRequestDTO.getPrice());
        inventory.setLastUpdated(new Date());

        // Simpan inventory ke database
        inventory = inventoryRepository.save(inventory);

        // Map kembali ke DTO untuk response
        return new InventoryResponseDTO(
                inventory.getId(),
                inventory.getSkuCode(),
                inventory.getQuantity(),
                inventory.getPrice(),
                inventory.getLastUpdated());
    }

    // Get inventory by SKU code
    @Override
    public InventoryResponseDTO getInventoryBySkuCode(String skuCode) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for SKU Code: " + skuCode));

        return mapToDTO(inventory);
    }

    // Get all inventories
    @Override
    public List<InventoryResponseDTO> getAllInventory() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public void updateInventory(Long id, UpdateInventoryRequestDTO updateInventoryRequestDTO) {
        // Find inventory by ID
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for ID: " + id));

        // Update quantity if provided
        if (updateInventoryRequestDTO.getQuantity() != null) {
            inventory.setQuantity(updateInventoryRequestDTO.getQuantity());
        }

        // Update price if provided
        if (updateInventoryRequestDTO.getPrice() != null) {
            inventory.setPrice(updateInventoryRequestDTO.getPrice());
        }

        // Update lastUpdated timestamp
        inventory.setLastUpdated(new Date());

        // Save updated inventory
        inventoryRepository.save(inventory);
    }

    // Add inventory item
    @Override
    public void addInventoryItem(InventoryResponseDTO inventoryResponseDTO) {
        Inventory inventory = new Inventory();
        inventory.setSkuCode(inventoryResponseDTO.getSkuCode());
        inventory.setQuantity(inventoryResponseDTO.getQuantity());
        inventory.setPrice(inventoryResponseDTO.getPrice());
        inventory.setLastUpdated(new Date());
        inventoryRepository.save(inventory);
    }

    // Delete inventory item
    @Override
    public void deleteInventoryItem(String skuCode) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for SKU Code: " + skuCode));

        inventoryRepository.delete(inventory);
    }

    // Helper method to map Inventory entity to DTO
    private InventoryResponseDTO mapToDTO(Inventory inventory) {
        return new InventoryResponseDTO(
                inventory.getId(),
                inventory.getSkuCode(),
                inventory.getQuantity(),
                inventory.getPrice(),
                inventory.getLastUpdated());
    }
}
