package com.e_commerce.notification_service.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.e_commerce.notification_service.dto.NotificationRequestDTO;
import com.e_commerce.notification_service.dto.event.UserLoginEventDTO;
import com.e_commerce.notification_service.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaNotificationListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public KafkaNotificationListener(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "user-login", groupId = "notification-service-group")
    public void listenUserLogin(ConsumerRecord<String, String> record) {
        log.info("Received Login Event: {}", record.value());

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Parsing pertama untuk mengubah String yang di-escape menjadi JSON murni
            String fixedJson = objectMapper.readValue(record.value(), String.class);
            log.info("Fixed JSON: {}", fixedJson);

            // Parsing kedua untuk mengonversi JSON menjadi DTO
            UserLoginEventDTO event = objectMapper.readValue(fixedJson, UserLoginEventDTO.class);
            log.info("Parsed Event Object: {}", event);

            NotificationRequestDTO request = new NotificationRequestDTO();
            request.setUserId(event.getUserId());
            request.setEventType("USER_LOGIN");
            request.setMessage("Halo, " + event.getUsername() + " selamat datang!");

            notificationService.createNotification(request);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse event message: {}", record.value(), e);
        } catch (Exception e) {
            log.error("Unexpected error while processing event: {}", record.value(), e);
        }
    }

}
