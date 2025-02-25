package com.e_commerce.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce.user_service.dto.CreateUserRequestDTO;
import com.e_commerce.user_service.dto.UpdateUserRequestDTO;
import com.e_commerce.user_service.dto.UserResponseDTO;
import com.e_commerce.user_service.dto.WalletUpdateDTO;
import com.e_commerce.user_service.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Create a new user
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody @Valid CreateUserRequestDTO createUserRequestDTO) {
        UserResponseDTO createdUser = userService.createUser(createUserRequestDTO);
        return ResponseEntity.status(201).body(createdUser); // 201 Created
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // Get a user by Email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        UserResponseDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    // Update a user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable String id,
            @RequestBody @Valid UpdateUserRequestDTO updateUserRequestDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, updateUserRequestDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // Delete a user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User with ID " + id + " has been deleted successfully.");
    }

    // Top-up Wallet
    @PostMapping("/{id}/wallet/topup")
    public ResponseEntity<UserResponseDTO> topUpWallet(
            @PathVariable String id,
            @RequestBody @Valid WalletUpdateDTO walletUpdateDTO) {
        UserResponseDTO updatedUser = userService.updateWallet(id, walletUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }
}
