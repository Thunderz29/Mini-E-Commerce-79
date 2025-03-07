package com.e_commerce.notification_service.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.e_commerce.notification_service.dto.NotificationRequestDTO;
import com.e_commerce.notification_service.dto.NotificationResponseDTO;
import com.e_commerce.notification_service.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

        private MockMvc mockMvc;

        @Mock
        private NotificationService notificationService;

        @InjectMocks
        private NotificationController notificationController;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @BeforeEach
        void setup() {
                mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        }

        @Test
        void testCreateNotification() throws Exception {
                NotificationRequestDTO requestDTO = NotificationRequestDTO.builder()
                                .userId("1234as12")
                                .eventType("ORDER_PLACED")
                                .message("Your order has been placed successfully.")
                                .build();

                NotificationResponseDTO responseDTO = NotificationResponseDTO.builder()
                                .id("5678as12")
                                .userId("1234as12")
                                .eventType("ORDER_PLACED")
                                .message("Your order has been placed successfully.")
                                .createdAt(LocalDateTime.now())
                                .build();

                when(notificationService.createNotification(any(NotificationRequestDTO.class))).thenReturn(responseDTO);

                mockMvc.perform(post("/notifications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.userId").value("1234as12"))
                                .andExpect(jsonPath("$.eventType").value("ORDER_PLACED"))
                                .andExpect(jsonPath("$.message").value("Your order has been placed successfully."))
                                .andExpect(jsonPath("$.status").value("SENT"));

                verify(notificationService).createNotification(any(NotificationRequestDTO.class));
        }

        @Test
        void testGetNotificationsByUserId() throws Exception {
                NotificationResponseDTO notification1 = NotificationResponseDTO.builder()
                                .id("5678as12")
                                .userId("1234as12")
                                .eventType("ORDER_PLACED")
                                .message("Your order has been placed successfully.")
                                .createdAt(LocalDateTime.now())
                                .build();

                NotificationResponseDTO notification2 = NotificationResponseDTO.builder()
                                .id("5678as12")
                                .userId("1234as12")
                                .eventType("ORDER_SHIPPED")
                                .message("Your order has been shipped.")
                                .createdAt(LocalDateTime.now())
                                .build();

                when(notificationService.getNotificationsByUserId("1234as12"))
                                .thenReturn(List.of(notification1, notification2));

                mockMvc.perform(get("/notifications/user/{userId}", "1234as12"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(2))
                                .andExpect(jsonPath("$[0].eventType").value("ORDER_PLACED"))
                                .andExpect(jsonPath("$[0].message").value("Your order has been placed successfully."))
                                .andExpect(jsonPath("$[0].status").value("SENT"))
                                .andExpect(jsonPath("$[1].eventType").value("ORDER_SHIPPED"))
                                .andExpect(jsonPath("$[1].message").value("Your order has been shipped."))
                                .andExpect(jsonPath("$[1].status").value("SENT"));

                verify(notificationService).getNotificationsByUserId("1234as12");
        }
}
