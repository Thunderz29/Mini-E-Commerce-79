package com.e_commerce.payment_service.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Integer orderId;
    private BigDecimal amount;
}
