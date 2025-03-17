package com.e_commerce.order_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_commerce.order_service.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // ✅ Native Query untuk mencari order berdasarkan userId
    @Query(value = "SELECT * FROM orders WHERE user_id = :userId", nativeQuery = true)
    List<Order> findByUserId(@Param("userId") UUID userId);
}
