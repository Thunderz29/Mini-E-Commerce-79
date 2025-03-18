package com.e_commerce.user_service.service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "user-created", groupId = "user-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenUserCreated(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        processEvent("User Created", record, acknowledgment);
    }

    @KafkaListener(topics = "wallet-updated", groupId = "user-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenWalletUpdated(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        processEvent("Wallet Updated", record, acknowledgment);
    }

    @KafkaListener(topics = "user-login", groupId = "user-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenUserLogin(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        processEvent("User Login", record, acknowledgment);
    }

    @KafkaListener(topics = "user-updated", groupId = "user-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenUserUpdated(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        processEvent("User Updated", record, acknowledgment);
    }

    @KafkaListener(topics = "user-retrieved", groupId = "user-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenUserRetrieved(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        processEvent("User Retrieved", record, acknowledgment);
    }

    @KafkaListener(topics = "forgot-password", groupId = "user-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenForgotPassword(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        processEvent("Forgot Password", record, acknowledgment);
    }

    private void processEvent(String eventType, ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            log.info("Processing {} Event: {}", eventType, record.value());

            // Simulasi pemrosesan data
            Thread.sleep(1000);

            // Jika sukses, commit offset agar Kafka tahu event sudah diproses
            acknowledgment.acknowledge();
            log.info("{} Event processed successfully", eventType);

        } catch (Exception e) {
            log.error("Error processing {} Event: {}", eventType, record.value(), e);
            // Jika error, offset tidak di-commit, Kafka akan mengirim ulang pesan
        }
    }
}
