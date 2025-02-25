package com.e_commerce.user_service.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicsConfig {
    public static final String USER_CREATED_TOPIC = "user.created";
}
