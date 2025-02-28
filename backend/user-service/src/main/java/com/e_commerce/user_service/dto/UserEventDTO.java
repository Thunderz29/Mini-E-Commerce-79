package com.e_commerce.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEventDTO {
    private String userId;
    private String username;
    private String email;
    private String eventType;
}
