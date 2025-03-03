package com.e_commerce.order_service.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_commerce.order_service.config.KafkaProducerService;
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
        private final KafkaProducerService kafkaProducerService;

        // create order
        @Override
        public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
                // Validasi jumlah produk yang dipesan harus lebih dari 0
                if (orderRequestDTO.getQuantity() <= 0) {
                        throw new InvalidOrderException("Quantity must be greater than zero.");
                }

                // // Kirim request ke Product Service untuk cek stok
                // String productServiceUrl =
                // "http://localhost:8080/product-service/products/check-stock";
                // RestTemplate restTemplate = new RestTemplate();

                // UriComponentsBuilder builder =
                // UriComponentsBuilder.fromHttpUrl(productServiceUrl)
                // .queryParam("orderId", UUID.randomUUID().toString())
                // .queryParam("productId", orderRequestDTO.getProductId())
                // .queryParam("quantity", orderRequestDTO.getQuantity());

                // restTemplate.postForEntity(builder.toUriString(), null, String.class);

                // Membuat objek Order dari DTO request
                Order order = Order.builder()
                                .userId(orderRequestDTO.getUserId())
                                .productId(orderRequestDTO.getProductId())
                                .quantity(orderRequestDTO.getQuantity())
                                .status("PENDING") // Status awal pesanan adalah PENDING
                                .build();

                // Simpan order ke dalam database
                Order savedOrder = orderRepository.save(order);

                // 🔥 Kirim event order_created ke Kafka
                // kafkaProducerService.sendMessage("order_created", savedOrder);

                // Mengembalikan response dalam bentuk DTO
                return mapToDTO(savedOrder);
        }

        // get order by id
        @Override
        public OrderResponseDTO getOrderById(UUID id) {
                // Mencari order berdasarkan ID, jika tidak ditemukan lempar exception
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));
                return mapToDTO(order);
        }

        // get all orders
        @Override
        public List<OrderResponseDTO> getAllOrders() {
                // Mengambil semua order dari database dan mengonversinya ke DTO
                return orderRepository.findAll().stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        // update order status
        @Override
        public OrderResponseDTO updateOrderStatus(UUID id, String status) {
                // Mencari order berdasarkan ID, jika tidak ditemukan lempar exception
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));

                // Order hanya bisa diubah jika statusnya masih PENDING
                if (!order.getStatus().equals("PENDING")) {
                        throw new RuntimeException("Cannot update order status after confirmation or cancellation.");
                }

                // Mengupdate status pesanan
                order.setStatus(status);
                Order updatedOrder = orderRepository.save(order);

                // Mengembalikan response DTO dari order yang diperbarui
                return mapToDTO(updatedOrder);
        }

        // cancel order
        @Override
        public void cancelOrder(UUID id) {
                // Mencari order berdasarkan ID, jika tidak ditemukan lempar exception
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));

                // Menghapus order dari database
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
