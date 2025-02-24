package com.e_commerce.product_service.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_commerce.product_service.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Custom query method untuk mencari produk berdasarkan kategori
    List<Product> findByCategory(String category);

    // Custom query method untuk mencari produk berdasarkan nama (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findAll(Sort sort);

}
