package com.e_commerce.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInventoryRequestDTO {
    private String skuCode;
    private Integer quantity;
    private Double price;
}
