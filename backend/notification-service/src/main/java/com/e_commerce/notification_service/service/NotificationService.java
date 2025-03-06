package com.e_commerce.notification_service.service;

import java.util.List;

import com.e_commerce.notification_service.dto.NotificationRequestDTO;
import com.e_commerce.notification_service.dto.NotificationResponseDTO;

public interface NotificationService {
    NotificationResponseDTO createNotification(NotificationRequestDTO request);

    List<NotificationResponseDTO> getNotificationsByUserId(String userId);
}
