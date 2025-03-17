package com.e_commerce.notification_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_commerce.notification_service.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    // ✅ Native Query untuk mencari semua notifikasi berdasarkan userId
    @Query(value = "SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC", nativeQuery = true)
    List<Notification> findByUserId(@Param("userId") String userId);
}
