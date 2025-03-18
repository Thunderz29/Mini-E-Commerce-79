package com.e_commerce.product_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_commerce.product_service.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    // ✅ JPQL Query untuk mencari produk berdasarkan kategori
    @Query("SELECT p FROM Product p WHERE p.category = :category")
    List<Product> findByCategory(@Param("category") String category);

    // ✅ JPQL Query untuk mencari produk berdasarkan nama (case-insensitive)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);

    // ✅ JPQL Query untuk mengambil semua produk dengan sorting (Sorting tidak bisa
    // langsung dengan JPQL)
    List<Product> findAll(Sort sort);

    // ✅ Tambahkan method untuk mencari produk berdasarkan ID
    Optional<Product> findById(String id);
}
