package com.e_commerce.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_commerce.user_service.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Query method to find User by email
    Optional<User> findByEmail(String email);

    // Query method to check if a username exists
    boolean existsByUsername(String username);

    // Query method to check if an email exists
    boolean existsByEmail(String email);
}
