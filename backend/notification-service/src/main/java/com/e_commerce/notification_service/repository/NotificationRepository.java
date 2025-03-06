package com.e_commerce.notification_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_commerce.notification_service.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    // Cari semua notifikasi berdasarkan userId
    List<Notification> findByUserId(String userId);

    // Cari semua notifikasi berdasarkan status
    List<Notification> findByStatus(String status);
}
