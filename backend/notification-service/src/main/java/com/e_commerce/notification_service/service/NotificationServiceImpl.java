package com.e_commerce.notification_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.e_commerce.notification_service.dto.NotificationRequestDTO;
import com.e_commerce.notification_service.dto.NotificationResponseDTO;
import com.e_commerce.notification_service.exception.NotificationException;
import com.e_commerce.notification_service.exception.NotificationNotFoundException;
import com.e_commerce.notification_service.model.Notification;
import com.e_commerce.notification_service.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    // Create Notification
    public NotificationResponseDTO createNotification(NotificationRequestDTO request) {
        if (request == null) {
            throw new NotificationException("Request tidak boleh null");
        }

        try {
            Notification notification = Notification.builder()
                    .userId(request.getUserId())
                    .eventType(request.getEventType())
                    .message(request.getMessage())
                    .createdAt(LocalDateTime.now())
                    .build();

            Notification savedNotification = notificationRepository.save(notification);
            log.info("Notification saved: {}", savedNotification);

            return modelMapper.map(savedNotification, NotificationResponseDTO.class);
        } catch (Exception e) {
            throw new NotificationException("Gagal membuat notifikasi: " + e.getMessage());
        }
    }

    // Get Notification by User ID
    @Override
    public List<NotificationResponseDTO> getNotificationsByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new NotificationException("User ID tidak boleh kosong");
        }

        try {
            List<Notification> notifications = notificationRepository.findByUserId(userId);

            if (notifications.isEmpty()) {
                throw new NotificationNotFoundException("No notifications found for user ID: " + userId);
            }

            return notifications.stream()
                    .map(notification -> modelMapper.map(notification, NotificationResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new NotificationException("Gagal mengambil notifikasi: " + e.getMessage());
        }
    }

    // // Menerima event dari Kafka
    // @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    // public void listenKafka(NotificationRequestDTO request) {
    // createNotification(request);
    // System.out.println("Received notification event: " + request);
    // }
}
