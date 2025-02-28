package com.e_commerce.order_service.config;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.e_commerce.order_service.dto.StockUpdateDTO;
import com.e_commerce.order_service.exception.OrderNotFoundException;
import com.e_commerce.order_service.model.Order;
import com.e_commerce.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "stock_update", groupId = "order-service-group")
    public void consumeStockUpdate(StockUpdateDTO stockUpdateDTO) {
        log.info("📥 Received stock update: {}", stockUpdateDTO);

        UUID orderId = UUID.fromString(stockUpdateDTO.getOrderId());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found."));

        if (stockUpdateDTO.isStockAvailable()) {
            order.setStatus("CONFIRMED");
        } else {
            order.setStatus("CANCELLED");
        }

        orderRepository.save(order);
        log.info("✅ Order {} status updated to: {}", orderId, order.getStatus());
    }
}
