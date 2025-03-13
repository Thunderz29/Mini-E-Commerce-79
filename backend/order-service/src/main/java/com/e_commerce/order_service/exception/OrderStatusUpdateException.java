package com.e_commerce.order_service.exception;

public class OrderStatusUpdateException extends RuntimeException {
    public OrderStatusUpdateException(String message) {
        super(message);
    }
}
