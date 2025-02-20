package com.e_commerce.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.e_commerce.user_service.dto.CreateUserRequestDTO;
import com.e_commerce.user_service.dto.LoginRequestDTO;
import com.e_commerce.user_service.dto.LoginResponseDTO;
import com.e_commerce.user_service.dto.UpdateUserRequestDTO;
import com.e_commerce.user_service.dto.UserResponseDTO;
import com.e_commerce.user_service.exception.DuplicateUserException;
import com.e_commerce.user_service.exception.UserNotFoundException;
import com.e_commerce.user_service.model.User;
import com.e_commerce.user_service.repository.UserRepository;
import com.e_commerce.user_service.security.JwtTokenProvider;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private JwtTokenProvider jwtTokenProvider;

    private PasswordEncoder passwordEncoder;

    // Create User
    @Override
    public void createUser(CreateUserRequestDTO createUserRequestDTO) {
        // Check if email already exists
        if (userRepository.existsByEmail(createUserRequestDTO.getEmail())) {
            throw new DuplicateUserException("Email already exists: " + createUserRequestDTO.getEmail());
        }

        // Check if username already exists
        if (userRepository.existsByUsername(createUserRequestDTO.getUsername())) {
            throw new DuplicateUserException("Username already exists: " + createUserRequestDTO.getUsername());
        }

        // Encode the password
        String encodedPassword = passwordEncoder.encode(createUserRequestDTO.getPassword());

        // Map DTO to entity and save
        User user = new User();
        user.setUsername(createUserRequestDTO.getUsername());
        user.setEmail(createUserRequestDTO.getEmail());
        user.setPassword(encodedPassword);

        // Save the user to the database
        userRepository.save(user);
    }

    // Get User by ID
    @Override
    public UserResponseDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        // Map User entity to UserResponseDTO
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt());
    }

    // Get User by Email
    @Override
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Convert User to UserResponseDTO
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt());
    }

    // Update User
    @Override
    public UserResponseDTO updateUser(String id, UpdateUserRequestDTO updateUserRequestDTO) {
        // Find user by ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        // Update fields if present in DTO
        if (updateUserRequestDTO.getUsername() != null) {
            user.setUsername(updateUserRequestDTO.getUsername());
        }
        if (updateUserRequestDTO.getEmail() != null) {
            // Check if email is used by another user
            if (userRepository.existsByEmail(updateUserRequestDTO.getEmail()) &&
                    !user.getEmail().equals(updateUserRequestDTO.getEmail())) {
                throw new DuplicateUserException("Email already exists: " + updateUserRequestDTO.getEmail());
            }
            user.setEmail(updateUserRequestDTO.getEmail());
        }
        if (updateUserRequestDTO.getPassword() != null) {
            // Set password directly
            user.setPassword(updateUserRequestDTO.getPassword());
        }

        // Save updated user
        user = userRepository.save(user);

        // Map user to UserResponseDTO and return
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt());
    }

    // Delete User
    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    // Login
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        // Check if email exists in the database
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Email not found: " + loginRequest.getEmail()));

        // Validate password using PasswordEncoder
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // Generate JWT Token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        // Return response
        return new LoginResponseDTO(token, "Login successful");
    }

}
