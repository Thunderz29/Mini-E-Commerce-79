package com.e_commerce.product_service.listener;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.e_commerce.product_service.dto.UserLoginEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaUserListener {

    private final ConcurrentHashMap<String, String> userSessionMap = new ConcurrentHashMap<>();

    @KafkaListener(topics = "user-login", groupId = "product-service-group")
    public void listenUserLogin(ConsumerRecord<String, String> record) {
        log.info("Received Kafka Event Raw: {}", record.value()); // Tambahkan log ini

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserLoginEventDTO userEvent = objectMapper.readValue(record.value(), UserLoginEventDTO.class);

            log.info("Parsed Kafka Event: {}", userEvent);

            userSessionMap.put(userEvent.getUserId(), userEvent.getToken());
            log.info("User {} login token cached successfully", userEvent.getUserId());
        } catch (Exception e) {
            log.error("Failed to parse Kafka event: {}", e.getMessage());
        }
    }

    public String getUserToken(String userId) {
        return userSessionMap.get(userId);
    }
}
