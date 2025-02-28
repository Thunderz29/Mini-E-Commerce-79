package com.e_commerce.product_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import com.e_commerce.product_service.config.KafkaProducerService;
import com.e_commerce.product_service.dto.ProductRequestDTO;
import com.e_commerce.product_service.dto.ProductResponseDTO;
import com.e_commerce.product_service.dto.StockUpdateDTO;
import com.e_commerce.product_service.exception.ResourceNotFoundException;
import com.e_commerce.product_service.model.Product;
import com.e_commerce.product_service.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MinioService minioService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequestDTO productRequestDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId(1L);
        product.setName("Test Product");
        product.setDescription("Product Description");
        product.setPrice(BigDecimal.valueOf(100));
        product.setCategory("Electronics");
        product.setQuantity(10);
        product.setImageUrl("http://example.com/image.jpg");

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Test Product");
        productRequestDTO.setDescription("Product Description");
        productRequestDTO.setPrice(BigDecimal.valueOf(100));
        productRequestDTO.setCategory("Electronics");
        productRequestDTO.setQuantity(10);
    }

    @Test
    void testCreateProduct_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO response = productService.createProduct(productRequestDTO);

        assertNotNull(response);
        assertEquals(product.getName(), response.getName());
        assertEquals(product.getCategory(), response.getCategory());
        assertEquals(0, product.getPrice().compareTo(response.getPrice()));

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testCreateProduct_WithImage() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(minioService.uploadFile(mockFile, "product-images")).thenReturn("http://example.com/new-image.jpg");

        productRequestDTO.setFile(mockFile);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO response = productService.createProduct(productRequestDTO);

        assertNotNull(response);
        assertEquals("http://example.com/image.jpg", response.getImageUrl());

        verify(minioService, times(1)).uploadFile(mockFile, "product-images");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDTO response = productService.getProductById(1L);

        assertNotNull(response);
        assertEquals(product.getName(), response.getName());
        assertEquals(product.getCategory(), response.getCategory());
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(product));

        List<ProductResponseDTO> response = productService.getAllProducts("name", "asc");

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(product.getName(), response.get(0).getName());

        verify(productRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testUpdateProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO response = productService.updateProduct(1L, productRequestDTO);

        assertNotNull(response);
        assertEquals(productRequestDTO.getName(), response.getName());
        assertEquals(productRequestDTO.getCategory(), response.getCategory());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(99L, productRequestDTO));
    }

    @Test
    void testDeleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
    }

    @Test
    void testCheckAndUpdateStock_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.checkAndUpdateStock("order123", 1L, 5);

        assertEquals(5, product.getQuantity());
        verify(productRepository, times(1)).save(product);
        verify(kafkaProducerService, times(1)).sendStockUpdate(any(StockUpdateDTO.class));
    }

    @Test
    void testCheckAndUpdateStock_InsufficientStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.checkAndUpdateStock("order123", 1L, 15);

        assertEquals(10, product.getQuantity()); // Stock tidak berkurang
        verify(productRepository, never()).save(any(Product.class));
        verify(kafkaProducerService, times(1)).sendStockUpdate(any(StockUpdateDTO.class));
    }

    @Test
    void testCheckAndUpdateStock_ProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.checkAndUpdateStock("order123", 99L, 5));
    }
}
