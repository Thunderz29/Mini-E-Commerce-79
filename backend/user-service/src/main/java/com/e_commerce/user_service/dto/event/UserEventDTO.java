package com.e_commerce.user_service.dto.event;

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
    private String phone;
    private String eventType;
}
