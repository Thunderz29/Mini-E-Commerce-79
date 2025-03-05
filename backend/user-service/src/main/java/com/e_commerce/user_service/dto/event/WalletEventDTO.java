package com.e_commerce.user_service.dto.event;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletEventDTO {
    private String userId;
    private String username;
    private BigDecimal amount;
    private BigDecimal walletBalance;
    private String eventType;
}
