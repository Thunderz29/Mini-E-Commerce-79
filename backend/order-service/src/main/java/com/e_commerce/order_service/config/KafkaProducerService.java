package com.e_commerce.order_service.config;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic, Object payload) {
        log.info("📤 Sending message to topic {}: {}", topic, payload);
        kafkaTemplate.send(topic, payload);
    }

}
