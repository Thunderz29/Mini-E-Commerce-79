package com.e_commerce.order_service.dto;

import lombok.Data;

@Data
public class StockUpdateDTO {
    private String orderId;
    private boolean stockAvailable;
}
