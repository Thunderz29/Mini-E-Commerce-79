package com.e_commerce.notification_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.e_commerce.notification_service.dto.NotificationRequestDTO;
import com.e_commerce.notification_service.dto.NotificationResponseDTO;
import com.e_commerce.notification_service.exception.NotificationNotFoundException;
import com.e_commerce.notification_service.model.Notification;
import com.e_commerce.notification_service.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification;
    private NotificationRequestDTO requestDTO;
    private NotificationResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new NotificationRequestDTO("1234as12", "ORDER_PLACED", "Your order has been placed.");
        notification = Notification.builder()
                .id("1674as12")
                .userId("1234as12")
                .eventType("ORDER_PLACED")
                .message("Your order has been placed.")
                .build();
        responseDTO = NotificationResponseDTO.builder()
                .id("5678as12")
                .userId("1234as12")
                .eventType("ORDER_PLACED")
                .message("Your order has been placed.")
                .build();
    }

    @Test
    void testCreateNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(modelMapper.map(any(Notification.class), eq(NotificationResponseDTO.class))).thenReturn(responseDTO);

        NotificationResponseDTO result = notificationService.createNotification(requestDTO);

        assertNotNull(result);
        assertEquals("1234as12", result.getUserId());
        assertEquals("ORDER_PLACED", result.getEventType());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testGetNotificationsByUserId_Success() {
        when(notificationRepository.findByUserId("1234as12")).thenReturn(Collections.singletonList(notification));
        when(modelMapper.map(any(Notification.class), eq(NotificationResponseDTO.class))).thenReturn(responseDTO);

        List<NotificationResponseDTO> result = notificationService.getNotificationsByUserId("1234as12");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("1234as12", result.get(0).getUserId());
    }

    @Test
    void testGetNotificationsByUserId_NotFound() {
        when(notificationRepository.findByUserId("1234as12")).thenReturn(Collections.emptyList());

        assertThrows(NotificationNotFoundException.class,
                () -> notificationService.getNotificationsByUserId("1234as12"));
    }
}
