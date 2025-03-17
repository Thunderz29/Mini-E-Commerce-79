package com.e_commerce.product_service.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_commerce.product_service.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // ✅ Native Query untuk mencari produk berdasarkan kategori
    @Query(value = "SELECT * FROM products WHERE category = :category", nativeQuery = true)
    List<Product> findByCategory(@Param("category") String category);

    // ✅ Native Query untuk mencari produk berdasarkan nama (case-insensitive)
    @Query(value = "SELECT * FROM products WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))", nativeQuery = true)
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);

    // ✅ Native Query untuk mengambil semua produk dengan sorting
    @Query(value = "SELECT * FROM products ORDER BY ?1", nativeQuery = true)
    List<Product> findAll(@Param("sort") Sort sort);

}
