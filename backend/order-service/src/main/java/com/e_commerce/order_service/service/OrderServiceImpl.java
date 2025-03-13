package com.e_commerce.order_service.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.e_commerce.order_service.config.KafkaProducerService;
import com.e_commerce.order_service.dto.OrderRequestDTO;
import com.e_commerce.order_service.dto.OrderResponseDTO;
import com.e_commerce.order_service.exception.InvalidOrderException;
import com.e_commerce.order_service.exception.OrderNotFoundException;
import com.e_commerce.order_service.exception.OrderProcessingException;
import com.e_commerce.order_service.exception.OrderStatusUpdateException;
import com.e_commerce.order_service.model.Order;
import com.e_commerce.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

        private OrderRepository orderRepository;
        private KafkaProducerService kafkaProducerService;

        // create order
        @Override
        public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
                if (orderRequestDTO == null) {
                        throw new InvalidOrderException("Order request cannot be null.");
                }

                if (orderRequestDTO.getProductId() == null) {
                        throw new InvalidOrderException("Product ID must not be null or empty.");
                }

                if (orderRequestDTO.getUserId() == null || orderRequestDTO.getUserId().isEmpty()) {
                        throw new InvalidOrderException("User ID must not be null or empty.");
                }

                if (orderRequestDTO.getQuantity() <= 0) {
                        throw new InvalidOrderException("Quantity must be greater than zero.");
                }

                String productServiceUrl = "http://localhost:8080/product-service/products/check-stock";
                RestTemplate restTemplate = new RestTemplate();

                try {
                        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(productServiceUrl)
                                        .queryParam("orderId", UUID.randomUUID().toString())
                                        .queryParam("productId", orderRequestDTO.getProductId())
                                        .queryParam("quantity", orderRequestDTO.getQuantity());

                        ResponseEntity<String> response = restTemplate.postForEntity(builder.toUriString(), null,
                                        String.class);

                        if (!response.getStatusCode().is2xxSuccessful()) {
                                throw new OrderProcessingException("Failed to verify stock with product service.");
                        }
                } catch (Exception e) {
                        throw new OrderProcessingException(
                                        "Error communicating with product service: " + e.getMessage(), e);
                }

                try {
                        Order order = Order.builder()
                                        .userId(orderRequestDTO.getUserId())
                                        .productId(orderRequestDTO.getProductId())
                                        .quantity(orderRequestDTO.getQuantity())
                                        .status("PENDING")
                                        .build();

                        Order savedOrder = orderRepository.save(order);

                        kafkaProducerService.sendMessage("order_created", savedOrder);

                        return mapToDTO(savedOrder);
                } catch (Exception e) {
                        throw new OrderProcessingException("Error saving order to database: " + e.getMessage(), e);
                }
        }

        // get order by id
        @Override
        public OrderResponseDTO getOrderById(UUID id) {
                if (id == null) {
                        throw new InvalidOrderException("Order ID cannot be null.");
                }

                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));

                return mapToDTO(order);
        }

        // get all orders
        @Override
        public List<OrderResponseDTO> getAllOrders() {
                List<Order> orders = orderRepository.findAll();

                if (orders.isEmpty()) {
                        throw new OrderNotFoundException("No orders found.");
                }

                return orders.stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        // update order status
        public OrderResponseDTO updateOrderStatus(UUID id, String status) {
                if (id == null || status == null || status.trim().isEmpty()) {
                        throw new InvalidOrderException("Order ID and status must not be null or empty.");
                }

                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));

                if (!order.getStatus().equals("PENDING")) {
                        throw new OrderStatusUpdateException(
                                        "Cannot update order status after confirmation or cancellation.");
                }

                order.setStatus(status);
                Order updatedOrder = orderRepository.save(order);

                return mapToDTO(updatedOrder);
        }

        // cancel order
        public void cancelOrder(UUID id) {
                if (id == null) {
                        throw new InvalidOrderException("Order ID cannot be null.");
                }

                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));

                if (!order.getStatus().equals("PENDING")) {
                        throw new OrderStatusUpdateException("Only pending orders can be canceled.");
                }

                orderRepository.delete(order);
        }

        // Method untuk mengonversi entitas Order menjadi DTO
        private OrderResponseDTO mapToDTO(Order order) {
                return OrderResponseDTO.builder()
                                .id(order.getId())
                                .userId(order.getUserId())
                                .productId(order.getProductId())
                                .quantity(order.getQuantity())
                                .status(order.getStatus())
                                .orderDate(order.getOrderDate())
                                .build();
        }
}
