package com.e_commerce.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.e_commerce.user_service.config.KafkaProducerService;
import com.e_commerce.user_service.dto.CreateUserRequestDTO;
import com.e_commerce.user_service.dto.LoginRequestDTO;
import com.e_commerce.user_service.dto.LoginResponseDTO;
import com.e_commerce.user_service.dto.UserResponseDTO;
import com.e_commerce.user_service.dto.WalletUpdateDTO;
import com.e_commerce.user_service.exception.DuplicateUserException;
import com.e_commerce.user_service.exception.UserNotFoundException;
import com.e_commerce.user_service.model.User;
import com.e_commerce.user_service.repository.UserRepository;
import com.e_commerce.user_service.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private CreateUserRequestDTO createUserRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("1");
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setWalletBalance(BigDecimal.ZERO);

        createUserRequestDTO = new CreateUserRequestDTO();
        createUserRequestDTO.setUsername("testUser");
        createUserRequestDTO.setEmail("test@example.com");
        createUserRequestDTO.setPassword("password");
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.createUser(createUserRequestDTO);

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(DuplicateUserException.class, () -> userService.createUser(createUserRequestDTO));
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        UserResponseDTO response = userService.getUserById("1");

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById("99")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById("99"));
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateToken(anyString(), anyString())).thenReturn("mockToken");

        LoginResponseDTO response = userService.login(new LoginRequestDTO("test@example.com", "password"));

        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getToken());
    }

    @Test
    void testLogin_InvalidPassword() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        LoginResponseDTO response = userService.login(new LoginRequestDTO("test@example.com", "wrongpassword"));

        assertEquals(401, response.getStatusCode());
        assertNull(response.getToken());
    }

    @Test
    void testUpdateWallet_Success() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        WalletUpdateDTO walletUpdateDTO = new WalletUpdateDTO(BigDecimal.valueOf(100));
        UserResponseDTO response = userService.updateWallet("1", walletUpdateDTO);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(100), response.getWalletBalance());
    }

    @Test
    void testUpdateWallet_UserNotFound() {
        when(userRepository.findById("99")).thenReturn(Optional.empty());
        WalletUpdateDTO walletUpdateDTO = new WalletUpdateDTO(BigDecimal.valueOf(100));

        assertThrows(UserNotFoundException.class, () -> userService.updateWallet("99", walletUpdateDTO));
    }
}
