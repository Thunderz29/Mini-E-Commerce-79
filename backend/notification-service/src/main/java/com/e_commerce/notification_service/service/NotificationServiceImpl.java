package com.e_commerce.notification_service.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.e_commerce.notification_service.dto.NotificationRequestDTO;
import com.e_commerce.notification_service.dto.NotificationResponseDTO;
import com.e_commerce.notification_service.exception.NotificationNotFoundException;
import com.e_commerce.notification_service.model.Notification;
import com.e_commerce.notification_service.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    @Override
    public NotificationResponseDTO createNotification(NotificationRequestDTO request) {
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .eventType(request.getEventType())
                .message(request.getMessage())
                .status("PENDING") // Status default
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return modelMapper.map(savedNotification, NotificationResponseDTO.class);
    }

    @Override
    public List<NotificationResponseDTO> getNotificationsByUserId(UUID userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);

        if (notifications.isEmpty()) {
            throw new NotificationNotFoundException("No notifications found for user ID: " + userId);
        }

        return notifications.stream()
                .map(notification -> modelMapper.map(notification, NotificationResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Menerima event dari Kafka
    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void listenKafka(NotificationRequestDTO request) {
        createNotification(request);
        System.out.println("Received notification event: " + request);
    }
}
