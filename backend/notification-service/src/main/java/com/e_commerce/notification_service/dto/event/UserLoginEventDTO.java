package com.e_commerce.notification_service.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor // Pastikan ini ada untuk deserialisasi
@JsonIgnoreProperties(ignoreUnknown = true) // Hindari error jika ada field tambahan di JSON
public class UserLoginEventDTO {

    private String userId;
    private String username;
    private String email;
    private String token;
    private String eventType;
}
