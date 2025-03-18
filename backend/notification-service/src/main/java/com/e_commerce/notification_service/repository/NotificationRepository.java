package com.e_commerce.notification_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_commerce.notification_service.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    // ✅ JPQL Query untuk mencari semua notifikasi berdasarkan userId dan urut
    // berdasarkan createdAt DESC
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    List<Notification> findByUserId(@Param("userId") String userId);
}
