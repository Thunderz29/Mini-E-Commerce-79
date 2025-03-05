package com.e_commerce.user_service.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "user-created", groupId = "user-service-group")
    public void listenUserCreated(ConsumerRecord<String, String> record) {
        log.info("Received Kafka Event: {}", record.value());
    }

    @KafkaListener(topics = "wallet-updated", groupId = "user-service-group")
    public void listenWalletUpdated(ConsumerRecord<String, String> record) {
        log.info("Received Kafka Event: {}", record.value());
    }

    @KafkaListener(topics = "user-login", groupId = "user-service-group")
    public void listenUserLogin(ConsumerRecord<String, String> record) {
        log.info("Received Login Event: {}", record.value());
    }

    @KafkaListener(topics = "user-updated", groupId = "user-service-group")
    public void listenUserUpdated(ConsumerRecord<String, String> record) {
        log.info("Received User Updated Event: {}", record.value());
    }

    @KafkaListener(topics = "user-retrieved", groupId = "user-service-group")
    public void listenUserRetrieved(ConsumerRecord<String, String> record) {
        log.info("Received User Retrieved Event: {}", record.value());
    }

    @KafkaListener(topics = "forgot-password", groupId = "user-service-group")
    public void listenForgotPassword(ConsumerRecord<String, String> record) {
        log.info("Received Forgot Password Event: {}", record.value());
    }
}
