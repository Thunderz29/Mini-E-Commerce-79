package com.e_commerce.user_service.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.e_commerce.user_service.dto.CreateUserRequestDTO;
import com.e_commerce.user_service.dto.UpdateUserRequestDTO;
import com.e_commerce.user_service.dto.UserResponseDTO;
import com.e_commerce.user_service.dto.WalletUpdateDTO;
import com.e_commerce.user_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

        private MockMvc mockMvc;

        @Mock
        private UserService userService;

        @InjectMocks
        private UserController userController;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @BeforeEach
        void setup() {
                mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        }

        @Test
        @DisplayName("✅ Should create a new user successfully")
        void testCreateUser() throws Exception {
                CreateUserRequestDTO requestDTO = new CreateUserRequestDTO();
                requestDTO.setUsername("John Doe");
                requestDTO.setEmail("john@example.com");
                requestDTO.setPassword("password123");

                UserResponseDTO responseDTO = new UserResponseDTO(201, "1", "John Doe", "john@example.com", "081283664",
                                BigDecimal.valueOf(100.0), LocalDateTime.now());

                when(userService.createUser(any(CreateUserRequestDTO.class))).thenReturn(responseDTO);

                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value("1"))
                                .andExpect(jsonPath("$.username").value("John Doe"))
                                .andExpect(jsonPath("$.email").value("john@example.com"))
                                .andExpect(jsonPath("$.walletBalance").value(100.0));

                verify(userService).createUser(any(CreateUserRequestDTO.class));
        }

        @Test
        @DisplayName("❌ Should return 400 Bad Request when creating user with invalid data")
        void testCreateUser_InvalidRequest() throws Exception {
                CreateUserRequestDTO invalidRequest = new CreateUserRequestDTO();
                invalidRequest.setUsername("");
                invalidRequest.setEmail("invalid-email");
                invalidRequest.setPassword("");

                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("✅ Should get user by ID")
        void testGetUserById() throws Exception {
                UserResponseDTO responseDTO = new UserResponseDTO(200, "1", "John Doe", "john@example.com", "081283664",
                                BigDecimal.valueOf(200.0), LocalDateTime.now());

                when(userService.getUserById("1")).thenReturn(responseDTO);

                mockMvc.perform(get("/users/{id}", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("1"))
                                .andExpect(jsonPath("$.username").value("John Doe"))
                                .andExpect(jsonPath("$.email").value("john@example.com"))
                                .andExpect(jsonPath("$.walletBalance").value(200.0));

                verify(userService).getUserById("1");
        }

        @Test
        @DisplayName("❌ Should return 404 Not Found when user does not exist")
        void testGetUserById_NotFound() throws Exception {
                when(userService.getUserById("1")).thenReturn(null);

                mockMvc.perform(get("/users/{id}", "1"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").exists())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.error").value("User not found"))
                                .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Test
        @DisplayName("✅ Should get user by Email")
        void testGetUserByEmail() throws Exception {
                UserResponseDTO responseDTO = new UserResponseDTO(200, "1", "John Doe", "john@example.com", "081283664",
                                BigDecimal.valueOf(200.0), LocalDateTime.now());

                when(userService.getUserByEmail("john@example.com")).thenReturn(responseDTO);

                mockMvc.perform(get("/users/email/{email}", "john@example.com"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("1"))
                                .andExpect(jsonPath("$.username").value("John Doe"))
                                .andExpect(jsonPath("$.email").value("john@example.com"))
                                .andExpect(jsonPath("$.walletBalance").value(200.0));

                verify(userService).getUserByEmail("john@example.com");
        }

        @Test
        @DisplayName("✅ Should update user successfully")
        void testUpdateUser() throws Exception {
                UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO(200, "John Updated",
                                "john.updated@example.com", "081283664");

                UserResponseDTO responseDTO = new UserResponseDTO(200, "1", "John Updated", "john.updated@example.com",
                                "081283664",
                                BigDecimal.valueOf(300.0), LocalDateTime.now());

                when(userService.updateUser(eq("1"), any(UpdateUserRequestDTO.class))).thenReturn(responseDTO);

                mockMvc.perform(put("/users/{id}", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("1"))
                                .andExpect(jsonPath("$.username").value("John Updated"))
                                .andExpect(jsonPath("$.email").value("john.updated@example.com"));

                verify(userService).updateUser(eq("1"), any(UpdateUserRequestDTO.class));
        }

        @Test
        @DisplayName("✅ Should delete user successfully")
        void testDeleteUser() throws Exception {
                doNothing().when(userService).deleteUser("1");

                mockMvc.perform(delete("/users/{id}", "1"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("User with ID 1 has been deleted successfully."));

                verify(userService).deleteUser("1");
        }

        @Test
        @DisplayName("✅ Should top-up user wallet")
        void testTopUpWallet() throws Exception {
                WalletUpdateDTO walletUpdateDTO = new WalletUpdateDTO(BigDecimal.valueOf(150.0));

                UserResponseDTO responseDTO = new UserResponseDTO(200, "1", "John Doe", "john@example.com", "081283664",
                                BigDecimal.valueOf(450.0), LocalDateTime.now());

                when(userService.updateWallet(eq("1"), any(WalletUpdateDTO.class))).thenReturn(responseDTO);

                mockMvc.perform(post("/users/{id}/wallet/topup", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(walletUpdateDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.walletBalance").value(450.0));

                verify(userService).updateWallet(eq("1"), any(WalletUpdateDTO.class));
        }
}
