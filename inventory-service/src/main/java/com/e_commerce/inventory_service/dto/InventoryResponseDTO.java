package com.e_commerce.inventory_service.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDTO {
    private Long id;
    private String skuCode;
    private Integer quantity;
    private Double price;
    private Date lastUpdated;
}
