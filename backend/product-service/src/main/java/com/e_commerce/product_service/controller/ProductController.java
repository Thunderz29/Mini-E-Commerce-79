package com.e_commerce.product_service.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.e_commerce.product_service.dto.ProductRequestDTO;
import com.e_commerce.product_service.dto.ProductResponseDTO;
import com.e_commerce.product_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    // POST /products - Menambah produk baru
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestParam("product") String productJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        try {
            log.info("Received request to create product: {}", productJson);

            ObjectMapper mapper = new ObjectMapper();
            ProductRequestDTO productRequest = mapper.readValue(productJson, ProductRequestDTO.class);
            productRequest.setFile(file);

            ProductResponseDTO createdProduct = productService.createProduct(productRequest);
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // GET /products/{id} - Mendapatkan detail produk berdasarkan ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable("id") Long productId) {
        ProductResponseDTO productResponse = productService.getProductById(productId);
        return ResponseEntity.ok(productResponse);
    }

    // GET /products - Menampilkan daftar produk
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(@RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        List<ProductResponseDTO> products = productService.getAllProducts(sortBy, direction);
        return ResponseEntity.ok(products);
    }

    // PUT /products/{id} - Mengupdate data produk
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") Long productId,
            @RequestParam("product") String productJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            ProductRequestDTO productRequest = mapper.readValue(productJson, ProductRequestDTO.class);
            productRequest.setFile(file);

            ProductResponseDTO updatedProduct = productService.updateProduct(productId, productRequest);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error update product: " + e.getMessage());
        }
    }

    // DELETE /products/{id} - Menghapus produk
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check-stock")
    public ResponseEntity<String> checkStock(
            @RequestParam String orderId,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        productService.checkAndUpdateStock(orderId, productId, quantity);
        return ResponseEntity.ok("Stock check request received.");
    }
}
