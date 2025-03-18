package com.e_commerce.cart_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.e_commerce.cart_service.dto.client.ProductResponseDTO;

@FeignClient(name = "product-service", url = "http://localhost:8080/product-service/products")
public interface ProductClient {

    @GetMapping("/{id}")
    ProductResponseDTO getProductById(@PathVariable("id") String productId);
}
