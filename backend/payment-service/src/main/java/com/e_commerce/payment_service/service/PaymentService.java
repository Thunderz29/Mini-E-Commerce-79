package com.e_commerce.payment_service.service;

import com.e_commerce.payment_service.dto.PaymentRequestDTO;
import com.e_commerce.payment_service.dto.PaymentResponseDTO;

public interface PaymentService {
    PaymentResponseDTO processPayment(PaymentRequestDTO requestDTO);

    PaymentResponseDTO getPaymentById(String paymentId);
}
