package com.e_commerce.cart_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_commerce.cart_service.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

    // ✅ JPQL Query untuk mencari CartItem berdasarkan cartId dan productId
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.productId = :productId")
    Optional<CartItem> findByCartIdAndProductId(@Param("cartId") String cartId, @Param("productId") String productId);
}
