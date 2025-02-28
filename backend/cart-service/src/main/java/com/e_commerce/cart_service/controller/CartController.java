package com.e_commerce.cart_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce.cart_service.dto.CartItemRequestDTO;
import com.e_commerce.cart_service.dto.CartResponseDTO;
import com.e_commerce.cart_service.dto.UpdateCartItemDTO;
import com.e_commerce.cart_service.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Mendapatkan keranjang berdasarkan userId
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    // Menambahkan produk ke dalam keranjang
    @PostMapping("/{userId}/add")
    public ResponseEntity<CartResponseDTO> addItemToCart(@PathVariable String userId,
            @RequestBody CartItemRequestDTO request) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, request));
    }

    // Mengupdate jumlah produk dalam keranjang
    @PutMapping("/{userId}/update")
    public ResponseEntity<CartResponseDTO> updateCartItem(@PathVariable String userId,
            @RequestBody UpdateCartItemDTO request) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, request));
    }

    // Menghapus produk dari keranjang
    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<CartResponseDTO> removeItemFromCart(@PathVariable String userId,
            @PathVariable String productId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userId, productId));
    }

    // Menghapus seluruh isi keranjang
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
