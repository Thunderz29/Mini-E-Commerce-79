package com.e_commerce.user_service.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_commerce.user_service.config.KafkaProducerService;
import com.e_commerce.user_service.dto.CreateUserRequestDTO;
import com.e_commerce.user_service.dto.ForgotPasswordDTO;
import com.e_commerce.user_service.dto.LoginRequestDTO;
import com.e_commerce.user_service.dto.LoginResponseDTO;
import com.e_commerce.user_service.dto.UpdateUserRequestDTO;
import com.e_commerce.user_service.dto.UserEventDTO;
import com.e_commerce.user_service.dto.UserResponseDTO;
import com.e_commerce.user_service.dto.WalletUpdateDTO;
import com.e_commerce.user_service.exception.DuplicateUserException;
import com.e_commerce.user_service.exception.ResourceNotFoundException;
import com.e_commerce.user_service.exception.UserNotFoundException;
import com.e_commerce.user_service.model.User;
import com.e_commerce.user_service.repository.UserRepository;
import com.e_commerce.user_service.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducerService kafkaProducerService;
    private static final String USER_CREATED_TOPIC = "user-created";
    private static final String WALLET_UPDATED_TOPIC = "wallet-updated";
    private static final String USER_LOGIN_TOPIC = "user-login";

    // Create User
    @Override
    public UserResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO) {
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

        // Create new user entity
        User user = new User();
        user.setUsername(createUserRequestDTO.getUsername());
        user.setEmail(createUserRequestDTO.getEmail());
        user.setPassword(encodedPassword);
        user.setWalletBalance(BigDecimal.ZERO); // Default wallet balance

        // Save user to database
        User savedUser = userRepository.save(user);

        // Kirim event ke Kafka
        UserEventDTO event = new UserEventDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(),
                "USER_CREATED");
        kafkaProducerService.sendMessage(USER_CREATED_TOPIC, event.toString());

        // Return response DTO with status code
        return new UserResponseDTO(
                201, // Status Code for Created
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getWalletBalance(),
                savedUser.getCreatedAt());
    }

    // Get User by ID
    @Override
    public UserResponseDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        return new UserResponseDTO(
                200, // Status code for success
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getWalletBalance(),
                user.getCreatedAt());
    }

    // Get User by Email
    @Override
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return new UserResponseDTO(
                200, // Status code for success
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getWalletBalance(),
                user.getCreatedAt());
    }

    // Update User
    @Override
    public UserResponseDTO updateUser(String id, UpdateUserRequestDTO updateUserRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        if (updateUserRequestDTO.getUsername() != null) {
            user.setUsername(updateUserRequestDTO.getUsername());
        }
        if (updateUserRequestDTO.getEmail() != null) {
            if (userRepository.existsByEmail(updateUserRequestDTO.getEmail()) &&
                    !user.getEmail().equals(updateUserRequestDTO.getEmail())) {
                throw new DuplicateUserException("Email already exists: " + updateUserRequestDTO.getEmail());
            }
            user.setEmail(updateUserRequestDTO.getEmail());
        }
        if (updateUserRequestDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateUserRequestDTO.getPassword()));
        }

        user = userRepository.save(user);
        return new UserResponseDTO(
                200,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getWalletBalance(),
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
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Email not found: " + loginRequest.getEmail()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return new LoginResponseDTO(401, null, "Invalid password");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        // Kirim event login ke Kafka
        UserEventDTO event = new UserEventDTO(user.getId(), user.getUsername(), user.getEmail(),
                "USER_LOGIN");
        kafkaProducerService.sendMessage(USER_LOGIN_TOPIC, event.toString());

        return new LoginResponseDTO(200, token, "Login successful");
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        User user = userRepository.findByEmail(forgotPasswordDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + forgotPasswordDTO.getEmail()));

        if (!forgotPasswordDTO.getNewPassword().equals(forgotPasswordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirmation password do not match.");
        }

        String encodedPassword = passwordEncoder.encode(forgotPasswordDTO.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    // Update Wallet (Top-Up)
    @Override
    @Transactional
    public UserResponseDTO updateWallet(String id, WalletUpdateDTO walletUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        user.setWalletBalance(user.getWalletBalance().add(walletUpdateDTO.getAmount()));
        user = userRepository.save(user);

        // Kirim event ke Kafka
        UserEventDTO event = new UserEventDTO(user.getId(), user.getUsername(), user.getEmail(),
                "WALLET_UPDATED");

        kafkaProducerService.sendMessage(WALLET_UPDATED_TOPIC, event.toString());
        return new UserResponseDTO(
                200,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getWalletBalance(),
                user.getCreatedAt());
    }
}
