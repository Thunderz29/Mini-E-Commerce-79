package com.e_commerce.payment_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PaymentResponseDTO {
    private Long id;
    private Integer orderId;
    private BigDecimal amount;
    private String paymentStatus;
    private LocalDateTime paymentDate;
}
