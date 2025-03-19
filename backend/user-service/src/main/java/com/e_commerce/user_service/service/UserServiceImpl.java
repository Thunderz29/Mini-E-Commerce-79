package com.e_commerce.user_service.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_commerce.user_service.dto.CreateUserRequestDTO;
import com.e_commerce.user_service.dto.CurrentUserDTO;
import com.e_commerce.user_service.dto.ForgotPasswordDTO;
import com.e_commerce.user_service.dto.LoginRequestDTO;
import com.e_commerce.user_service.dto.LoginResponseDTO;
import com.e_commerce.user_service.dto.UpdateUserRequestDTO;
import com.e_commerce.user_service.dto.UserResponseDTO;
import com.e_commerce.user_service.dto.WalletUpdateDTO;
import com.e_commerce.user_service.dto.event.ForgotPasswordEventDTO;
import com.e_commerce.user_service.dto.event.UserEventDTO;
import com.e_commerce.user_service.dto.event.UserLoginEventDTO;
import com.e_commerce.user_service.dto.event.UserRetrievedEventDTO;
import com.e_commerce.user_service.dto.event.UserUpdatedEventDTO;
import com.e_commerce.user_service.dto.event.WalletEventDTO;
import com.e_commerce.user_service.exception.DuplicateUserException;
import com.e_commerce.user_service.exception.UserException;
import com.e_commerce.user_service.exception.UserNotFoundException;
import com.e_commerce.user_service.model.User;
import com.e_commerce.user_service.repository.UserRepository;
import com.e_commerce.user_service.security.JwtTokenProvider;
import com.e_commerce.user_service.service.kafka.KafkaProducerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
    private static final String USER_UPDATE_TOPIC = "user-updated";
    private static final String USER_RETREIVE_TOPIC = "user-retrieved";
    private static final String USER_FORGOT_PASSWORD_TOPIC = "forgot-password";

    // Create User
    @Override
    public UserResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO) {
        if (createUserRequestDTO == null) {
            throw new UserException("Request tidak boleh null");
        }

        if (createUserRequestDTO.getEmail() == null || createUserRequestDTO.getEmail().trim().isEmpty()) {
            throw new UserException("Email tidak boleh kosong");
        }

        if (createUserRequestDTO.getUsername() == null || createUserRequestDTO.getUsername().trim().isEmpty()) {
            throw new UserException("Username tidak boleh kosong");
        }

        if (createUserRequestDTO.getPassword() == null || createUserRequestDTO.getPassword().trim().isEmpty()) {
            throw new UserException("Password tidak boleh kosong");
        }

        if (userRepository.existsByEmail(createUserRequestDTO.getEmail())) {
            throw new DuplicateUserException("Email already exists: " + createUserRequestDTO.getEmail());
        }

        if (userRepository.existsByUsername(createUserRequestDTO.getUsername())) {
            throw new DuplicateUserException("Username already exists: " + createUserRequestDTO.getUsername());
        }

        try {
            String encodedPassword = passwordEncoder.encode(createUserRequestDTO.getPassword());

            User user = new User();
            user.setUsername(createUserRequestDTO.getUsername());
            user.setEmail(createUserRequestDTO.getEmail());
            user.setPassword(encodedPassword);
            user.setWalletBalance(BigDecimal.ZERO);
            user.setPhone(createUserRequestDTO.getPhone());

            User savedUser = userRepository.save(user);

            UserEventDTO event = new UserEventDTO(savedUser.getId(),
                    savedUser.getUsername(),
                    savedUser.getEmail(),
                    savedUser.getPhone(),
                    "USER_CREATED");
            kafkaProducerService.sendMessage(USER_CREATED_TOPIC, event.toString());

            return new UserResponseDTO(
                    201,
                    savedUser.getId(),
                    savedUser.getUsername(),
                    savedUser.getEmail(),
                    savedUser.getPhone(),
                    savedUser.getWalletBalance(),
                    savedUser.getCreatedAt());

        } catch (Exception e) {
            throw new UserException("Gagal membuat user: " + e.getMessage());
        }
    }

    // Get User by ID
    @Override
    public UserResponseDTO getUserById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new UserException("User ID tidak boleh kosong");
        }

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

            UserRetrievedEventDTO event = new UserRetrievedEventDTO(user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(), "USER_RETRIEVED");
            kafkaProducerService.sendMessage(USER_RETREIVE_TOPIC, event.toString());

            return new UserResponseDTO(
                    200,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getWalletBalance(),
                    user.getCreatedAt());

        } catch (Exception e) {
            throw new UserException("Gagal mengambil user: " + e.getMessage());
        }
    }

    // Get User by Email
    @Override
    public UserResponseDTO getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new UserException("Email tidak boleh kosong");
        }

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

            UserRetrievedEventDTO event = new UserRetrievedEventDTO(user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(), "USER_RETRIEVED");
            kafkaProducerService.sendMessage(USER_RETREIVE_TOPIC, event.toString());

            return new UserResponseDTO(
                    200,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getWalletBalance(),
                    user.getCreatedAt());

        } catch (Exception e) {
            throw new UserException("Gagal mengambil user: " + e.getMessage());
        }
    }

    // Update User
    @Override
    @Transactional
    public UserResponseDTO updateUser(String id, UpdateUserRequestDTO updateUserRequestDTO) {
        if (id == null || id.trim().isEmpty()) {
            throw new UserException("User ID tidak boleh kosong");
        }

        String tokenUserId;
        try {
            tokenUserId = jwtTokenProvider.getClaims(
                    SecurityContextHolder.getContext().getAuthentication().getCredentials().toString()).getSubject();
        } catch (Exception e) {
            throw new SecurityException("Gagal memverifikasi token pengguna");
        }

        if (!tokenUserId.equals(id)) {
            throw new SecurityException("Unauthorized to update this user");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        try {

            if (updateUserRequestDTO.getUsername() != null) {
                user.setUsername(updateUserRequestDTO.getUsername());
            }

            if (updateUserRequestDTO.getPhone() != null) {
                user.setPhone(updateUserRequestDTO.getPhone());
            }

            userRepository.save(user);

            UserUpdatedEventDTO event = new UserUpdatedEventDTO(user.getId(), user.getUsername(), user.getEmail(),
                    user.getPhone(), "USER_UPDATED");
            kafkaProducerService.sendMessage(USER_UPDATE_TOPIC, event);

            return new UserResponseDTO(
                    200,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getWalletBalance(),
                    user.getCreatedAt());
        } catch (Exception e) {
            throw new UserException("Gagal memperbarui user: " + e.getMessage());
        }
    }

    // Delete User
    @Override
    public void deleteUser(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new UserException("User ID tidak boleh kosong");
        }

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with ID: " + id);
        }

        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new UserException("Gagal menghapus user: " + e.getMessage());
        }
    }

    // login user
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email tidak boleh kosong");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password tidak boleh kosong");
        }

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Email not found: " + loginRequest.getEmail()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new SecurityException("Email atau password salah.");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        UserLoginEventDTO event = new UserLoginEventDTO(user.getId(), user.getUsername(), user.getEmail(),
                "USER_LOGIN");

        try {
            String jsonEvent = new ObjectMapper().writeValueAsString(event);
            kafkaProducerService.sendMessage(USER_LOGIN_TOPIC, jsonEvent);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize UserLoginEventDTO", e);
            throw new UserException("Gagal memproses login event");
        }

        return new LoginResponseDTO(200, token, "Login successful");
    }

    // Forgot Password User
    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        if (forgotPasswordDTO.getEmail() == null || forgotPasswordDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email tidak boleh kosong");
        }
        if (forgotPasswordDTO.getNewPassword() == null || forgotPasswordDTO.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password baru tidak boleh kosong");
        }
        if (!forgotPasswordDTO.getNewPassword().equals(forgotPasswordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Password dan konfirmasi password tidak cocok.");
        }

        User user = userRepository.findByEmail(forgotPasswordDTO.getEmail())
                .orElseThrow(
                        () -> new UserNotFoundException("User not found with email: " + forgotPasswordDTO.getEmail()));

        String encodedPassword = passwordEncoder.encode(forgotPasswordDTO.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        ForgotPasswordEventDTO event = new ForgotPasswordEventDTO(user.getId(), user.getEmail(), "FORGOT_PASSWORD");

        try {
            String jsonEvent = new ObjectMapper().writeValueAsString(event);
            kafkaProducerService.sendMessage(USER_FORGOT_PASSWORD_TOPIC, jsonEvent);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ForgotPasswordEventDTO", e);
            throw new UserException("Gagal memproses forgot password event");
        }
    }

    // Update Wallet (Top-Up)
    @Override
    @Transactional
    public UserResponseDTO updateWallet(String id, WalletUpdateDTO walletUpdateDTO) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID tidak boleh kosong");
        }
        if (walletUpdateDTO.getAmount() == null || walletUpdateDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Jumlah top-up harus lebih besar dari 0");
        }

        String tokenUserId;
        try {
            tokenUserId = jwtTokenProvider.getClaims(
                    SecurityContextHolder.getContext().getAuthentication().getCredentials().toString()).getSubject();
        } catch (Exception e) {
            throw new SecurityException("Gagal memverifikasi token pengguna");
        }

        if (!tokenUserId.equals(id)) {
            throw new SecurityException("Unauthorized to update wallet");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        user.setWalletBalance(user.getWalletBalance().add(walletUpdateDTO.getAmount()));
        user = userRepository.save(user);

        WalletEventDTO walletEvent = new WalletEventDTO(
                user.getId(),
                user.getUsername(),
                walletUpdateDTO.getAmount(),
                user.getWalletBalance(),
                "WALLET_UPDATED");

        try {
            String jsonEvent = new ObjectMapper().writeValueAsString(walletEvent);
            kafkaProducerService.sendMessage(WALLET_UPDATED_TOPIC, jsonEvent);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize WalletEventDTO", e);
            throw new UserException("Gagal memproses wallet event");
        }

        return new UserResponseDTO(
                200,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getWalletBalance(),
                user.getCreatedAt());
    }

    // Get Current User Login
    @Override
    public CurrentUserDTO getCurrentLoggedInUser() {
        try {
            String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
            String userId = jwtTokenProvider.getClaims(token).getSubject();

            // Verify that the user exists in the database
            userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

            return new CurrentUserDTO(200, userId, "User ID retrieved successfully");
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new SecurityException("Failed to get current user ID: " + e.getMessage());
        }
    }

}
