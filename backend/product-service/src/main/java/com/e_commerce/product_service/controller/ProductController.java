package com.e_commerce.product_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce.product_service.dto.ProductRequestDTO;
import com.e_commerce.product_service.dto.ProductResponseDTO;
import com.e_commerce.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // POST /products - Menambah produk baru
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO productRequest) {
        ProductResponseDTO createdProduct = productService.createProduct(productRequest);
        return ResponseEntity.ok(createdProduct);
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
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable("id") Long productId,
            @RequestBody ProductRequestDTO productRequest) {
        ProductResponseDTO updatedProduct = productService.updateProduct(productId, productRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    // DELETE /products/{id} - Menghapus produk
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
