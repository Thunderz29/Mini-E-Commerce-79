package com.e_commerce.cart_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_commerce.cart_service.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    // ✅ Native Query untuk mencari cart berdasarkan userId
    @Query(value = "SELECT * FROM carts WHERE user_id = :userId LIMIT 1", nativeQuery = true)
    Optional<Cart> findByUserId(@Param("userId") String userId);
}
