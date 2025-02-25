package com.e_commerce.payment_service.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_commerce.payment_service.dto.PaymentRequestDTO;
import com.e_commerce.payment_service.dto.PaymentResponseDTO;
import com.e_commerce.payment_service.exception.PaymentNotFoundException;
import com.e_commerce.payment_service.model.Payment;
import com.e_commerce.payment_service.repository.PaymentRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO requestDTO) {
        Payment payment = new Payment();
        payment.setOrderId(requestDTO.getOrderId());
        payment.setAmount(requestDTO.getAmount());
        payment.setPaymentStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());

        payment = paymentRepository.save(payment);

        return mapToDTO(payment);
    }

    @Override
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));

        return mapToDTO(payment);
    }

    private PaymentResponseDTO mapToDTO(Payment payment) {
        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        responseDTO.setId(payment.getId());
        responseDTO.setOrderId(payment.getOrderId());
        responseDTO.setAmount(payment.getAmount());
        responseDTO.setPaymentStatus(payment.getPaymentStatus());
        responseDTO.setPaymentDate(payment.getPaymentDate());
        return responseDTO;
    }
}
