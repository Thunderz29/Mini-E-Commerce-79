package com.e_commerce.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginEventDTO {
    private String userId;
    private String username;
    private String email;
    private String token;
    private String eventType;
}
