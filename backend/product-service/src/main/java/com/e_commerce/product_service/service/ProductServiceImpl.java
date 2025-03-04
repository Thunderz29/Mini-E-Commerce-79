package com.e_commerce.product_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.e_commerce.product_service.config.KafkaProducerService;
import com.e_commerce.product_service.dto.ProductRequestDTO;
import com.e_commerce.product_service.dto.ProductResponseDTO;
import com.e_commerce.product_service.dto.StockUpdateDTO;
import com.e_commerce.product_service.exception.ResourceNotFoundException;
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

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return mapToResponseDTO(product);
    }

    @Override
    public Page<ProductResponseDTO> getAllProducts(String sortBy, String direction, int page, int size) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Ambil data dalam bentuk Page<Product>
        Page<Product> productsPage = productRepository.findAll(pageable);

        // Gunakan .map() bawaan dari Page untuk konversi Product -> ProductResponseDTO
        return productsPage.map(this::mapToResponseDTO);
    }

    @Override
    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produk dengan ID " + productId + " tidak ditemukan"));

        // ✅ Update data produk
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setCategory(productRequestDTO.getCategory());
        product.setQuantity(productRequestDTO.getQuantity());

        if (productRequestDTO.getFile() != null && !productRequestDTO.getFile().isEmpty()) {
            try {
                // Hapus gambar lama jika ada
                // if (product.getImageUrl() != null) {
                // minioService.deleteFile("product-images", product.getImageUrl());
                // }

                // Upload gambar baru
                String newImageUrl = minioService.uploadFile(productRequestDTO.getFile(), "product-images");
                product.setImageUrl(newImageUrl);
            } catch (Exception e) {
                log.error("❌ Gagal mengganti foto produk: {}", e.getMessage());
                throw new RuntimeException("Gagal mengganti foto produk, silakan coba lagi.");
            }
        }

        Product updatedProduct = productRepository.save(product);
        return mapToResponseDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        productRepository.delete(product);
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

    @Transactional
    public void checkAndUpdateStock(String orderId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        boolean stockAvailable = product.getQuantity() >= quantity;

        if (stockAvailable) {
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
        }

        StockUpdateDTO stockUpdateDTO = new StockUpdateDTO(orderId, stockAvailable);
        kafkaProducerService.sendStockUpdate(stockUpdateDTO);
        log.info("✅ Stock update sent for order {}: {}", orderId, stockAvailable);
    }

}
