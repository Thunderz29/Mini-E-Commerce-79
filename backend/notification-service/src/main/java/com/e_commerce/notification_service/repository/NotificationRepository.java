package com.e_commerce.notification_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_commerce.notification_service.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Cari semua notifikasi berdasarkan userId
    List<Notification> findByUserId(UUID userId);

    // Cari semua notifikasi berdasarkan status
    List<Notification> findByStatus(String status);
}
