package com.e_commerce.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_commerce.user_service.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // ✅ JPQL Query untuk mencari User berdasarkan email
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    // ✅ JPQL Query untuk mengecek apakah username sudah ada
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    // ✅ JPQL Query untuk mengecek apakah email sudah ada
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);
}
