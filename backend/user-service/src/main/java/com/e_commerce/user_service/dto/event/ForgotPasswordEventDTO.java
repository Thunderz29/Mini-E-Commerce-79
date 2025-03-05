package com.e_commerce.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordEventDTO {
    private String email;
    private String eventType = "FORGOT_PASSWORD";
}
