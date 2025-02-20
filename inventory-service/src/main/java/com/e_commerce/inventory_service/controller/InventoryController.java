package com.e_commerce.inventory_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce.inventory_service.dto.InventoryRequestDTO;
import com.e_commerce.inventory_service.dto.InventoryResponseDTO;
import com.e_commerce.inventory_service.dto.UpdateInventoryRequestDTO;
import com.e_commerce.inventory_service.service.InventoryService;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // Create Inventory
    @PostMapping
    public ResponseEntity<InventoryResponseDTO> createInventory(@RequestBody InventoryRequestDTO inventoryRequestDTO) {
        InventoryResponseDTO responseDTO = inventoryService.createInventory(inventoryRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // Get All Inventory
    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventory() {
        List<InventoryResponseDTO> responseList = inventoryService.getAllInventory();
        return ResponseEntity.ok(responseList);
    }

    // Get Inventory by SKU Code
    @GetMapping("/{skuCode}")
    public ResponseEntity<InventoryResponseDTO> getInventoryBySkuCode(@PathVariable String skuCode) {
        InventoryResponseDTO responseDTO = inventoryService.getInventoryBySkuCode(skuCode);
        return ResponseEntity.ok(responseDTO);
    }

    // Update Inventory by Id
    @PutMapping("/{id}")
    public ResponseEntity<String> updateInventory(
            @PathVariable Long id,
            @RequestBody UpdateInventoryRequestDTO updateInventoryRequestDTO) {
        inventoryService.updateInventory(id, updateInventoryRequestDTO);
        return ResponseEntity.ok("Inventory updated successfully.");
    }

    // Delete Inventory
    @DeleteMapping("/{skuCode}")
    public ResponseEntity<String> deleteInventory(@PathVariable String skuCode) {
        inventoryService.deleteInventoryItem(skuCode);
        return ResponseEntity.ok("Inventory with SKU Code: " + skuCode + " has been deleted successfully.");
    }
}
