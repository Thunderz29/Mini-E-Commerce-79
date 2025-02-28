package com.e_commerce.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockUpdateDTO {
    private String orderId;
    private boolean stockAvailable;
}
