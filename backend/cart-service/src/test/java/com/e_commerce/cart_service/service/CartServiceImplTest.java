package com.e_commerce.cart_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.e_commerce.cart_service.dto.CartItemRequestDTO;
import com.e_commerce.cart_service.dto.CartResponseDTO;
import com.e_commerce.cart_service.dto.UpdateCartItemDTO;
import com.e_commerce.cart_service.exception.CartNotFoundException;
import com.e_commerce.cart_service.exception.ProductNotFoundException;
import com.e_commerce.cart_service.model.Cart;
import com.e_commerce.cart_service.model.CartItem;
import com.e_commerce.cart_service.repository.CartItemRepository;
import com.e_commerce.cart_service.repository.CartRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setId("cart123");
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>()); // Pastikan list tidak null

        cartItem = new CartItem();
        cartItem.setId("item123");
        cartItem.setCart(cart);
        cartItem.setProductId("product123");
        cartItem.setQuantity(2);

        cart.getItems().add(cartItem);
    }

    @Test
    void testGetCartByUserId_CartExists() {
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(cart));

        CartResponseDTO response = cartService.getCartByUserId("user123");

        assertNotNull(response);
        assertEquals("user123", response.getUserId());
    }

    @Test
    void testGetCartByUserId_CartNotFound() {
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> cartService.getCartByUserId("user123"));
    }

    @Test
    void testAddItemToCart_NewItem() {
        CartItemRequestDTO request = new CartItemRequestDTO("product123", 3);

        when(cartRepository.findByUserId("user123")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartItemRepository.findByCartIdAndProductId(anyString(), anyString())).thenReturn(Optional.empty());

        CartResponseDTO response = cartService.addItemToCart("user123", request);

        assertNotNull(response);
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void testUpdateCartItemQuantity_ProductExists() {
        UpdateCartItemDTO request = new UpdateCartItemDTO("product123", 5);
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), "product123"))
                .thenReturn(Optional.of(cartItem));

        CartResponseDTO response = cartService.updateCartItemQuantity("user123", request);

        assertNotNull(response);
        assertEquals(5, cartItem.getQuantity());
        verify(cartItemRepository, times(1)).save(cartItem);
    }

    @Test
    void testUpdateCartItemQuantity_ProductNotFound() {
        UpdateCartItemDTO request = new UpdateCartItemDTO("product123", 5);
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), "product123"))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> cartService.updateCartItemQuantity("user123", request));
    }

    @Test
    void testRemoveItemFromCart_Success() {
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), "product123"))
                .thenReturn(Optional.of(cartItem));

        CartResponseDTO response = cartService.removeItemFromCart("user123", "product123");

        assertNotNull(response);
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    void testClearCart_Success() {
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(cart));
        doNothing().when(cartItemRepository).deleteAll(anyList());

        assertDoesNotThrow(() -> cartService.clearCart("user123"));
        verify(cartItemRepository, times(1)).deleteAll(cart.getItems());
    }
}
