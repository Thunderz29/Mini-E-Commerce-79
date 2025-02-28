package com.e_commerce.product_service.config;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.e_commerce.product_service.dto.StockUpdateDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, StockUpdateDTO> kafkaTemplate;

    public void sendStockUpdate(StockUpdateDTO stockUpdateDTO) {
        log.info("📤 Sending stock update: {}", stockUpdateDTO);
        kafkaTemplate.send("stock_update", stockUpdateDTO);
    }
}
