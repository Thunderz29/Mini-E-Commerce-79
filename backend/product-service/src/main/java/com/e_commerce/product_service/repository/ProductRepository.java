package com.e_commerce.product_service.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.e_commerce.product_service.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // Custom query method untuk mencari produk berdasarkan kategori
    List<Product> findByCategory(String category);

    // Custom query method untuk mencari produk berdasarkan nama (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findAll(Sort sort);

    // ✅ Query untuk update imageUrl berdasarkan productId
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.imageUrl = :imageUrl WHERE p.productId = :productId")
    void updateProductImage(@Param("productId") Long productId, @Param("imageUrl") String imageUrl);

}
