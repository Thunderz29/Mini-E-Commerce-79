package com.e_commerce.notification_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordEventDTO {
    private String userId;
    private String email;
    private String eventType;
}
