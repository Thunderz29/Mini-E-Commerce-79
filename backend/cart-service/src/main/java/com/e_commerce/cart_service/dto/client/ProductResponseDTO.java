package com.e_commerce.cart_service.dto.client;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private String productId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String category;
    private String imageUrl;
}
