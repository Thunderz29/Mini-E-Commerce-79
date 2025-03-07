package com.e_commerce.order_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.e_commerce.order_service.dto.OrderRequestDTO;
import com.e_commerce.order_service.dto.OrderResponseDTO;
import com.e_commerce.order_service.exception.OrderNotFoundException;
import com.e_commerce.order_service.model.Order;
import com.e_commerce.order_service.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        order = Order.builder()
                .id(orderId)
                .userId("2134hf21")
                .productId(UUID.randomUUID())
                .quantity(2)
                .status("PENDING")
                .build();
    }

    @Test
    void createOrder_ShouldReturnOrderResponseDTO() {
        OrderRequestDTO requestDTO = new OrderRequestDTO(order.getUserId(), order.getProductId(), order.getQuantity());

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDTO response = orderService.createOrder(requestDTO);

        assertNotNull(response);
        assertEquals(order.getId(), response.getId());
        assertEquals(order.getUserId(), response.getUserId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void getOrderById_WhenOrderExists_ShouldReturnOrderResponseDTO() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderResponseDTO response = orderService.getOrderById(orderId);

        assertNotNull(response);
        assertEquals(order.getId(), response.getId());
        assertEquals(order.getStatus(), response.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void getOrderById_WhenOrderNotExists_ShouldThrowException() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(orderId));

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatusAndReturnResponseDTO() {
        String newStatus = "SHIPPED";

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDTO response = orderService.updateOrderStatus(orderId, newStatus);

        assertEquals(newStatus, response.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void cancelOrder_ShouldDeleteOrder() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.cancelOrder(orderId);

        verify(orderRepository, times(1)).delete(order);
    }
}
