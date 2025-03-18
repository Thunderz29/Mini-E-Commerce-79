package com.e_commerce.user_service.service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(String topic, Object event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            log.info("Generated JSON: {}", message);

            kafkaTemplate.send(topic, message);
            log.info("Sent Kafka Event: {}", message);
        } catch (Exception e) {
            log.error("Failed to send Kafka Event", e);
        }
    }

}
