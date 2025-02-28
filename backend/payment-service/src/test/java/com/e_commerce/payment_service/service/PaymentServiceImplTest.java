package com.e_commerce.payment_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.e_commerce.payment_service.dto.PaymentRequestDTO;
import com.e_commerce.payment_service.dto.PaymentResponseDTO;
import com.e_commerce.payment_service.exception.PaymentNotFoundException;
import com.e_commerce.payment_service.model.Payment;
import com.e_commerce.payment_service.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentRequestDTO paymentRequestDTO;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId(1L);
        payment.setOrderId("Order123"); // Integer sesuai model
        payment.setAmount(BigDecimal.valueOf(500)); // Pastikan ini BigDecimal
        payment.setPaymentStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());

        paymentRequestDTO = new PaymentRequestDTO();
        paymentRequestDTO.setOrderId("Order123");
        paymentRequestDTO.setAmount(BigDecimal.valueOf(500));
    }

    @Test
    void testProcessPayment_Success() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentResponseDTO response = paymentService.processPayment(paymentRequestDTO);

        assertNotNull(response);
        assertEquals(payment.getId(), response.getId());
        assertEquals(payment.getOrderId(), response.getOrderId());

        // Perbaiki perbandingan amount menggunakan compareTo()
        assertEquals(0, payment.getAmount().compareTo(response.getAmount()));
        assertEquals(payment.getPaymentStatus(), response.getPaymentStatus());
    }

    @Test
    void testGetPaymentById_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentResponseDTO response = paymentService.getPaymentById(1L);

        assertNotNull(response);
        assertEquals(payment.getId(), response.getId());
        assertEquals(payment.getOrderId(), response.getOrderId());

        // Perbaiki perbandingan amount menggunakan compareTo()
        assertEquals(0, payment.getAmount().compareTo(response.getAmount()));
        assertEquals(payment.getPaymentStatus(), response.getPaymentStatus());
    }

    @Test
    void testGetPaymentById_NotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentById(99L));
    }
}
