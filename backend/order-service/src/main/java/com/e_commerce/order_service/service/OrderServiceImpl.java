package com.e_commerce.order_service.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_commerce.order_service.dto.OrderRequestDTO;
import com.e_commerce.order_service.dto.OrderResponseDTO;
import com.e_commerce.order_service.exception.InvalidOrderException;
import com.e_commerce.order_service.exception.OrderNotFoundException;
import com.e_commerce.order_service.model.Order;
import com.e_commerce.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        if (orderRequestDTO.getQuantity() <= 0) {
            throw new InvalidOrderException("Quantity must be greater than zero.");
        }

        Order order = Order.builder()
                .userId(orderRequestDTO.getUserId())
                .productId(orderRequestDTO.getProductId())
                .quantity(orderRequestDTO.getQuantity())
                .status("PENDING")
                .build();

        Order savedOrder = orderRepository.save(order);
        return mapToDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));
        return mapToDTO(order);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO updateOrderStatus(UUID id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        return mapToDTO(updatedOrder);
    }

    @Override
    public void cancelOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));

        orderRepository.delete(order);
    }

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
