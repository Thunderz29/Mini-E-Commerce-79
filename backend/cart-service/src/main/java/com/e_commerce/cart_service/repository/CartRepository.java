package com.e_commerce.cart_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_commerce.cart_service.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    // ✅ JPQL Query untuk mencari cart berdasarkan userId
    @Query("SELECT c FROM Cart c WHERE c.userId = :userId")
    Optional<Cart> findByUserId(@Param("userId") String userId);
}
