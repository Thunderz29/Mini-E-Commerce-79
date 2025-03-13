package com.e_commerce.product_service.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.e_commerce.product_service.config.KafkaProducerService;
import com.e_commerce.product_service.dto.ProductRequestDTO;
import com.e_commerce.product_service.dto.ProductResponseDTO;
import com.e_commerce.product_service.dto.StockUpdateDTO;
import com.e_commerce.product_service.exception.BadRequestException;
import com.e_commerce.product_service.exception.ProductException;
import com.e_commerce.product_service.exception.ProductNotFoundException;
import com.e_commerce.product_service.exception.ResourceNotFoundException;
import com.e_commerce.product_service.listener.KafkaUserListener;
import com.e_commerce.product_service.model.Product;
import com.e_commerce.product_service.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MinioService minioService;
    private final KafkaProducerService kafkaProducerService;
    private final KafkaUserListener kafkaUserListener;

    // Create Product
    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setCategory(productRequestDTO.getCategory());
        product.setQuantity(productRequestDTO.getQuantity());

        try {
            if (productRequestDTO.getFile() != null && !productRequestDTO.getFile().isEmpty()) {
                String imageUrl = minioService.uploadFile(productRequestDTO.getFile(), "product-images");
                product.setImageUrl(imageUrl);
            }
        } catch (Exception e) {
            log.error("❌ Gagal mengupload gambar ke MinIO: {}", e.getMessage());
            throw new RuntimeException("Gagal mengupload gambar, silakan coba lagi.");
        }

        try {
            Product savedProduct = productRepository.save(product);
            return mapToResponseDTO(savedProduct);
        } catch (Exception e) {
            log.error("❌ Gagal menyimpan produk ke database: {}", e.getMessage());
            throw new RuntimeException("Gagal menyimpan produk, silakan coba lagi.");
        }
    }

    // Get Product by ID
    @Override
    public ProductResponseDTO getProductById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Product ID must be a positive number");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        return mapToResponseDTO(product);
    }

    @Override
    @Transactional
    public Page<ProductResponseDTO> getAllProducts(String sortBy, String direction, int page, int size) {
        if (page < 0 || size <= 0) {
            throw new BadRequestException("Page number must be 0 or greater, and size must be greater than 0");
        }

        if (sortBy == null || sortBy.trim().isEmpty()) {
            throw new BadRequestException("Sort field cannot be empty");
        }

        if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
            throw new BadRequestException("Sort direction must be 'asc' or 'desc'");
        }

        Sort sort;
        try {
            sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid sorting field: " + sortBy);
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productsPage = productRepository.findAll(pageable);

        if (productsPage.isEmpty()) {
            throw new ProductNotFoundException("No products found");
        }

        return productsPage.map(this::mapToResponseDTO);
    }

    // Update Product
    @Override
    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produk dengan ID " + productId + " tidak ditemukan"));

        if (productRequestDTO.getName() == null || productRequestDTO.getName().trim().isEmpty()) {
            throw new BadRequestException("Nama produk tidak boleh kosong");
        }
        if (productRequestDTO.getDescription() == null || productRequestDTO.getDescription().trim().isEmpty()) {
            throw new BadRequestException("Deskripsi produk tidak boleh kosong");
        }
        if (productRequestDTO.getPrice() == null || productRequestDTO.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Harga produk tidak boleh negatif");
        }
        if (productRequestDTO.getQuantity() < 0) {
            throw new BadRequestException("Jumlah stok tidak boleh negatif");
        }

        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setCategory(productRequestDTO.getCategory());
        product.setQuantity(productRequestDTO.getQuantity());

        if (productRequestDTO.getFile() != null && !productRequestDTO.getFile().isEmpty()) {
            try {
                String newImageUrl = minioService.uploadFile(productRequestDTO.getFile(), "product-images");
                product.setImageUrl(newImageUrl);
            } catch (Exception e) {
                log.error("❌ Gagal mengganti foto produk: {}", e.getMessage());
                throw new ProductException("Gagal mengganti foto produk, silakan coba lagi.");
            }
        }

        Product updatedProduct = productRepository.save(product);
        return mapToResponseDTO(updatedProduct);
    }

    // Delete Product
    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        try {
            productRepository.delete(product);
            log.info("✅ Produk dengan ID {} berhasil dihapus", id);
        } catch (Exception e) {
            log.error("❌ Gagal menghapus produk: {}", e.getMessage());
            throw new ProductException("Gagal menghapus produk, silakan coba lagi.");
        }
    }

    // Check And Update Stock
    @Transactional
    public void checkAndUpdateStock(String orderId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (quantity <= 0) {
            throw new BadRequestException("Jumlah produk harus lebih dari 0");
        }

        if (product.getQuantity() < quantity) {
            throw new ProductException("Stok tidak mencukupi untuk produk dengan ID: " + productId);
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        StockUpdateDTO stockUpdateDTO = new StockUpdateDTO(orderId, true);
        kafkaProducerService.sendStockUpdate(stockUpdateDTO);
        log.info("✅ Stock update sent for order {}: {}", orderId, true);
    }

    // Utility method to map Product entity to ProductResponseDTO
    private ProductResponseDTO mapToResponseDTO(Product product) {
        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setProductId(product.getProductId());
        responseDTO.setName(product.getName());
        responseDTO.setDescription(product.getDescription());
        responseDTO.setPrice(product.getPrice());
        responseDTO.setCategory(product.getCategory());
        responseDTO.setQuantity(product.getQuantity());
        responseDTO.setImageUrl(product.getImageUrl());

        return responseDTO;
    }

}
