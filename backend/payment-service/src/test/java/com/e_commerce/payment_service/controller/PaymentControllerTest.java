package com.e_commerce.payment_service.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.e_commerce.payment_service.dto.PaymentRequestDTO;
import com.e_commerce.payment_service.dto.PaymentResponseDTO;
import com.e_commerce.payment_service.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    void testProcessPayment_Success() throws Exception {
        // Given
        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setOrderId("ORD123");
        requestDTO.setAmount(new BigDecimal("150.00"));

        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        responseDTO.setId("1");
        responseDTO.setOrderId("ORD123");
        responseDTO.setAmount(new BigDecimal("150.00"));
        responseDTO.setPaymentStatus("COMPLETED");
        responseDTO.setPaymentDate(LocalDateTime.now());

        when(paymentService.processPayment(any(PaymentRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk()) // Bisa diganti 201 Created jika diubah di controller
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderId").value("ORD123"))
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.paymentStatus").value("COMPLETED"));

        verify(paymentService).processPayment(any(PaymentRequestDTO.class));
    }

    @Test
    void testProcessPayment_InvalidRequest() throws Exception {
        PaymentRequestDTO invalidRequest = new PaymentRequestDTO(); // Tidak ada orderId & amount (invalid)

        mockMvc.perform(post("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPaymentById_Success() throws Exception {
        // Given
        String paymentId = "1";
        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        responseDTO.setId(paymentId);
        responseDTO.setOrderId("ORD123");
        responseDTO.setAmount(new BigDecimal("200.00"));
        responseDTO.setPaymentStatus("COMPLETED");
        responseDTO.setPaymentDate(LocalDateTime.now());

        when(paymentService.getPaymentById(paymentId)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/payment/{paymentId}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId))
                .andExpect(jsonPath("$.orderId").value("ORD123"))
                .andExpect(jsonPath("$.amount").value(200.00))
                .andExpect(jsonPath("$.paymentStatus").value("COMPLETED"));

        verify(paymentService).getPaymentById(paymentId);
    }

    @Test
    void testGetPaymentById_NotFound() throws Exception {
        // Given
        String paymentId = "1";
        when(paymentService.getPaymentById(paymentId)).thenThrow(new RuntimeException("Payment not found"));

        // When & Then
        mockMvc.perform(get("/payment/{paymentId}", paymentId))
                .andExpect(status().isNotFound()) // ✅ Sekarang akan mengembalikan 404
                .andExpect(content().string("Payment not found"));
    }

}
