package com.e_commerce.user_service.service;

import com.e_commerce.user_service.dto.CreateUserRequestDTO;
import com.e_commerce.user_service.dto.ForgotPasswordDTO;
import com.e_commerce.user_service.dto.LoginRequestDTO;
import com.e_commerce.user_service.dto.LoginResponseDTO;
import com.e_commerce.user_service.dto.UpdateUserRequestDTO;
import com.e_commerce.user_service.dto.UserResponseDTO;

public interface UserService {

    // Create User
    void createUser(CreateUserRequestDTO createUserRequestDTO);

    // Get User by ID
    UserResponseDTO getUserById(String id);

    // Get User by Email
    UserResponseDTO getUserByEmail(String email);

    // Update User
    UserResponseDTO updateUser(String id, UpdateUserRequestDTO updateUserRequestDTO);

    // Delete User
    void deleteUser(String id);

    // Login
    LoginResponseDTO login(LoginRequestDTO loginRequest);

    // Forgot Password
    void forgotPassword(ForgotPasswordDTO forgotPasswordDTO);

}
