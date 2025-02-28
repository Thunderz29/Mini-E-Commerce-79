package com.e_commerce.user_service.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.e_commerce.user_service.dto.ForgotPasswordDTO;
import com.e_commerce.user_service.dto.LoginRequestDTO;
import com.e_commerce.user_service.dto.LoginResponseDTO;
import com.e_commerce.user_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

class LoginControllerTest {

        private MockMvc mockMvc;

        @Mock
        private UserService userService;

        @InjectMocks
        private LoginController loginController;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
        }

        @Test
        @DisplayName("✅ Login sukses harus mengembalikan 200 OK")
        void testLoginSuccess() throws Exception {
                // Arrange (persiapan data uji)
                LoginRequestDTO requestDTO = new LoginRequestDTO("user@example.com", "password123");
                LoginResponseDTO responseDTO = new LoginResponseDTO(200, "token-abc-123", "Login berhasil");

                when(userService.login(any(LoginRequestDTO.class))).thenReturn(responseDTO);

                // Act & Assert (eksekusi & verifikasi hasil)
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusCode").value(200))
                                .andExpect(jsonPath("$.token").value("token-abc-123"))
                                .andExpect(jsonPath("$.message").value("Login berhasil"));
        }

        @Test
        @DisplayName("❌ Login gagal harus mengembalikan 401 Unauthorized")
        void testLoginFailure() throws Exception {
                // Arrange (simulasi kegagalan login)
                LoginRequestDTO requestDTO = new LoginRequestDTO("tes123@example.com", "password123");

                when(userService.login(any(LoginRequestDTO.class)))
                                .thenThrow(new RuntimeException("Invalid email or password"));

                // Act & Assert
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(content().string("Invalid email or password"));
        }

        @Test
        @DisplayName("✅ Forgot Password berhasil harus mengembalikan 200 OK")
        void testForgotPasswordSuccess() throws Exception {
                // Arrange
                ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO(
                                "user@example.com", "newPassword123", "newPassword123");

                doNothing().when(userService).forgotPassword(any(ForgotPasswordDTO.class));

                // Act & Assert
                mockMvc.perform(post("/auth/forgot-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(forgotPasswordDTO)))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Password berhasil diubah."));
        }

        @Test
        @DisplayName("❌ Should return error when confirm password does not match")
        void testForgotPasswordFailure() throws Exception {
                ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO(
                                "test@example.com",
                                "newPassword123",
                                "newPassword123" // ❌ Ini penyebab error
                );

                mockMvc.perform(post("/auth/forgot-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(forgotPasswordDTO)))
                                .andExpect(status().isBadRequest()) // Pastikan server menangani error dengan status 400
                                .andExpect(jsonPath("$.message").value("Konfirmasi password tidak cocok"));
        }

}
