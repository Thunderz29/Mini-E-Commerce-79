package com.e_commerce.cart_service.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.e_commerce.cart_service.dto.CartItemRequestDTO;
import com.e_commerce.cart_service.dto.CartResponseDTO;
import com.e_commerce.cart_service.dto.UpdateCartItemDTO;
import com.e_commerce.cart_service.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    void testGetCart() throws Exception {
        String userId = "user123";
        CartResponseDTO cartResponse = new CartResponseDTO();

        when(cartService.getCartByUserId(userId)).thenReturn(cartResponse);

        mockMvc.perform(get("/api/cart/{userId}", userId))
                .andExpect(status().isOk());

        verify(cartService).getCartByUserId(userId);
    }

    @Test
    void testAddItemToCart() throws Exception {
        String userId = "user123";
        CartItemRequestDTO requestDTO = new CartItemRequestDTO("product1", 2);
        CartResponseDTO responseDTO = new CartResponseDTO();

        when(cartService.addItemToCart(eq(userId), any(CartItemRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/cart/{userId}/add", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        verify(cartService).addItemToCart(eq(userId), any(CartItemRequestDTO.class));
    }

    @Test
    void testUpdateCartItem() throws Exception {
        String userId = "user123";
        UpdateCartItemDTO requestDTO = new UpdateCartItemDTO("product1", 5);
        CartResponseDTO responseDTO = new CartResponseDTO();

        when(cartService.updateCartItemQuantity(eq(userId), any(UpdateCartItemDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/cart/{userId}/update", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        verify(cartService).updateCartItemQuantity(eq(userId), any(UpdateCartItemDTO.class));
    }

    @Test
    void testRemoveItemFromCart() throws Exception {
        String userId = "user123";
        String productId = "product1";
        CartResponseDTO responseDTO = new CartResponseDTO();

        when(cartService.removeItemFromCart(userId, productId)).thenReturn(responseDTO);

        mockMvc.perform(delete("/api/cart/{userId}/remove/{productId}", userId, productId))
                .andExpect(status().isOk());

        verify(cartService).removeItemFromCart(userId, productId);
    }

    @Test
    void testClearCart() throws Exception {
        String userId = "user123";

        doNothing().when(cartService).clearCart(userId);

        mockMvc.perform(delete("/api/cart/{userId}/clear", userId))
                .andExpect(status().isNoContent());

        verify(cartService).clearCart(userId);
    }
}
