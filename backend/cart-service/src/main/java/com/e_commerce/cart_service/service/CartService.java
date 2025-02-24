package com.e_commerce.cart_service.service;

import com.e_commerce.cart_service.dto.CartItemRequestDTO;
import com.e_commerce.cart_service.dto.CartResponseDTO;
import com.e_commerce.cart_service.dto.UpdateCartItemDTO;

public interface CartService {

    // Mendapatkan keranjang berdasarkan userId
    CartResponseDTO getCartByUserId(String userId);

    // Menambahkan produk ke dalam keranjang
    CartResponseDTO addItemToCart(String userId, CartItemRequestDTO request);

    // Mengupdate jumlah produk dalam keranjang
    CartResponseDTO updateCartItemQuantity(String userId, UpdateCartItemDTO request);

    // Menghapus produk dari keranjang
    CartResponseDTO removeItemFromCart(String userId, String productId);

    // Menghapus seluruh isi keranjang
    void clearCart(String userId);
}
