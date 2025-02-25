package com.e_commerce.order_service.service;

import java.util.List;
import java.util.UUID;

import com.e_commerce.order_service.dto.OrderRequestDTO;
import com.e_commerce.order_service.dto.OrderResponseDTO;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO);

    OrderResponseDTO getOrderById(UUID id);

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO updateOrderStatus(UUID id, String status);

    void cancelOrder(UUID id);
}
