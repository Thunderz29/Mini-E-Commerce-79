package com.e_commerce.cart_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequestDTO {
    private String productId;
    private int quantity;
}
