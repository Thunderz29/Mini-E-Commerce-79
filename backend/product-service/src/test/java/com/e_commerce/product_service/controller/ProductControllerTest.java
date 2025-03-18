package com.e_commerce.product_service.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.e_commerce.product_service.dto.ProductRequestDTO;
import com.e_commerce.product_service.dto.ProductResponseDTO;
import com.e_commerce.product_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

        private MockMvc mockMvc;

        @Mock
        private ProductService productService;

        @InjectMocks
        private ProductController productController;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @BeforeEach
        void setup() {
                mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        }

        @Test
        @DisplayName("✅ Should create a new product successfully")
        void testCreateProduct() throws Exception {
                ProductRequestDTO requestDTO = new ProductRequestDTO();
                requestDTO.setName("Test Product");
                requestDTO.setDescription("This is a test product");
                requestDTO.setPrice(new BigDecimal("100.00"));
                requestDTO.setQuantity(10);
                requestDTO.setCategory("Electronics");

                ProductResponseDTO responseDTO = new ProductResponseDTO("123", "Test Product", "This is a test product",
                                new BigDecimal("100.00"), 10, "Electronics", "image.jpg");

                MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg",
                                "image-content".getBytes());
                MockMultipartFile productJson = new MockMultipartFile("product", "", "application/json",
                                objectMapper.writeValueAsBytes(requestDTO));

                when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(responseDTO);

                mockMvc.perform(multipart("/products")
                                .file(file)
                                .file(productJson)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Test Product"))
                                .andExpect(jsonPath("$.description").value("This is a test product"))
                                .andExpect(jsonPath("$.price").value(100.00))
                                .andExpect(jsonPath("$.quantity").value(10))
                                .andExpect(jsonPath("$.category").value("Electronics"));

                verify(productService).createProduct(any(ProductRequestDTO.class));
        }

        @Test
        @DisplayName("✅ Should get product by ID")
        void testGetProductById() throws Exception {
                ProductResponseDTO responseDTO = new ProductResponseDTO("123", "Test Product", "Description",
                                new BigDecimal("100.00"), 10, "Electronics", "image.jpg");

                when(productService.getProductById("123")).thenReturn(responseDTO);

                mockMvc.perform(get("/products/{id}", "123"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.productId").value(1))
                                .andExpect(jsonPath("$.name").value("Test Product"))
                                .andExpect(jsonPath("$.price").value(100.00))
                                .andExpect(jsonPath("$.quantity").value(10))
                                .andExpect(jsonPath("$.category").value("Electronics"));

                verify(productService).getProductById("123");
        }

        // @Test
        // @DisplayName("✅ Should return all products")
        // void testGetAllProducts() throws Exception {
        // ProductResponseDTO product1 = new ProductResponseDTO(1L, "Product A", "Desc
        // A", new BigDecimal("50.00"), 5,
        // "Books", "imageA.jpg");
        // ProductResponseDTO product2 = new ProductResponseDTO(2L, "Product B", "Desc
        // B", new BigDecimal("75.00"), 8,
        // "Fashion", "imageB.jpg");

        // when(productService.getAllProducts(anyString(),
        // anyString())).thenReturn(List.of(product1, product2));

        // mockMvc.perform(get("/products"))
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("$.size()").value(2))
        // .andExpect(jsonPath("$[0].category").value("Books"))
        // .andExpect(jsonPath("$[1].category").value("Fashion"));

        // verify(productService).getAllProducts(anyString(), anyString());
        // }

        @Test
        @DisplayName("✅ Should update a product successfully")
        void testUpdateProduct() throws Exception {
                ProductRequestDTO requestDTO = new ProductRequestDTO();
                requestDTO.setName("Updated Product");
                requestDTO.setDescription("Updated Description");
                requestDTO.setPrice(new BigDecimal("150.00"));
                requestDTO.setQuantity(20);
                requestDTO.setCategory("Gaming");

                ProductResponseDTO responseDTO = new ProductResponseDTO("123", "Updated Product", "Updated Description",
                                new BigDecimal("150.00"), 20, "Gaming", "updated.jpg");

                MockMultipartFile file = new MockMultipartFile("file", "updated.jpg", "image/jpeg",
                                "updated-image-content".getBytes());
                MockMultipartFile productJson = new MockMultipartFile("product", "product.json", "application/json",
                                objectMapper.writeValueAsString(requestDTO).getBytes());

                when(productService.updateProduct(eq("123"), any(ProductRequestDTO.class))).thenReturn(responseDTO);

                mockMvc.perform(multipart("/products/{id}", 1L)
                                .file(file)
                                .file(productJson)
                                .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.category").value("Gaming"));

                verify(productService).updateProduct(eq("123"), any(ProductRequestDTO.class));
        }

        @Test
        @DisplayName("✅ Should delete a product")
        void testDeleteProduct() throws Exception {
                doNothing().when(productService).deleteProduct("123");

                mockMvc.perform(delete("/products/{id}", 1L))
                                .andExpect(status().isNoContent());

                verify(productService).deleteProduct("123");
        }

        @Test
        @DisplayName("✅ Should check stock successfully")
        void testCheckStock() throws Exception {
                doNothing().when(productService).checkAndUpdateStock("ORDER123", "123", 2);

                mockMvc.perform(post("/products/check-stock")
                                .param("orderId", "ORDER123")
                                .param("productId", "1")
                                .param("quantity", "2"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Stock check request received."));

                verify(productService).checkAndUpdateStock("ORDER123", "123", 2);
        }
}
