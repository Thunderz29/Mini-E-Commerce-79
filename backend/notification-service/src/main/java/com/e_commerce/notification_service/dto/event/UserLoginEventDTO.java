package com.e_commerce.notification_service.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoginEventDTO {

    private String userId;
    private String username;
    private String email;
    private String token;
    private String eventType;
}
