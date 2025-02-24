package com.e_commerce.cart_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartItemDTO {
    private String productId;
    private int quantity;
}
