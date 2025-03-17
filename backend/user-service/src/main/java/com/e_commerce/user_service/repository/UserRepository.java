package com.e_commerce.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e_commerce.user_service.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Native Query untuk mencari User berdasarkan email
    @Query(value = "SELECT * FROM users WHERE email = :email LIMIT 1", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    // Native Query untuk mengecek apakah username sudah ada
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM users WHERE username = :username", nativeQuery = true)
    boolean existsByUsername(@Param("username") String username);

    // Native Query untuk mengecek apakah email sudah ada
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM users WHERE email = :email", nativeQuery = true)
    boolean existsByEmail(@Param("email") String email);
}
