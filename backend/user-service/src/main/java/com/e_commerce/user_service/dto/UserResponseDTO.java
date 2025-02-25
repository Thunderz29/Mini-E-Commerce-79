package com.e_commerce.user_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private int statusCode;
    private String id;
    private String username;
    private String email;
    private BigDecimal walletBalance;
    private LocalDateTime createdAt;
}
