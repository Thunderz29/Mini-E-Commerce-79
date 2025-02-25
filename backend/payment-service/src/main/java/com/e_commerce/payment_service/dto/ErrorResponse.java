package com.e_commerce.payment_service.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private int status;
    private LocalDateTime timestamp;
}
