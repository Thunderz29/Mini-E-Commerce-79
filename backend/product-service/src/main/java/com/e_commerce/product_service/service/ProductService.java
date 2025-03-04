package com.e_commerce.product_service.service;

import org.springframework.data.domain.Page;

import com.e_commerce.product_service.dto.ProductRequestDTO;
import com.e_commerce.product_service.dto.ProductResponseDTO;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);

    ProductResponseDTO getProductById(Long id);

    Page<ProductResponseDTO> getAllProducts(String sortBy, String direction, int page, int size);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO);

    void deleteProduct(Long id);

    void checkAndUpdateStock(String orderId, Long productId, int quantity);
}
