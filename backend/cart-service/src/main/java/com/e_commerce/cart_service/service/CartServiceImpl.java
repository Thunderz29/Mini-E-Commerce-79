package com.e_commerce.cart_service.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_commerce.cart_service.dto.CartItemRequestDTO;
import com.e_commerce.cart_service.dto.CartItemResponseDTO;
import com.e_commerce.cart_service.dto.CartResponseDTO;
import com.e_commerce.cart_service.dto.UpdateCartItemDTO;
import com.e_commerce.cart_service.exception.CartNotFoundException;
import com.e_commerce.cart_service.exception.ProductNotFoundException;
import com.e_commerce.cart_service.model.Cart;
import com.e_commerce.cart_service.model.CartItem;
import com.e_commerce.cart_service.repository.CartItemRepository;
import com.e_commerce.cart_service.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    // Mendapatkan keranjang berdasarkan userId
    @Override
    public CartResponseDTO getCartByUserId(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

        return convertToCartResponseDTO(cart);
    }

    // Menambahkan produk ke dalam keranjang
    @Override
    public CartResponseDTO addItemToCart(String userId, CartItemRequestDTO request) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return cartRepository.save(newCart);
        });

        // Cek apakah produk sudah ada dalam keranjang
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(),
                request.getProductId());

        if (existingItem.isPresent()) {
            // Jika produk sudah ada, update jumlahnya
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            // Jika belum ada, tambahkan produk baru
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }

        return convertToCartResponseDTO(cart);
    }

    // Mengupdate jumlah produk dalam keranjang
    @Override
    public CartResponseDTO updateCartItemQuantity(String userId, UpdateCartItemDTO request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found in cart"));

        // Jika jumlah = 0, hapus produk dari keranjang
        if (request.getQuantity() <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }

        return convertToCartResponseDTO(cart);
    }

    // Menghapus produk dari keranjang
    @Override
    public CartResponseDTO removeItemFromCart(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found in cart"));

        cartItemRepository.delete(cartItem);

        return convertToCartResponseDTO(cart);
    }

    // Menghapus seluruh isi keranjang
    @Override
    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

        cartItemRepository.deleteAll(cart.getItems());
    }

    // Helper method untuk mengubah Cart ke CartResponseDTO
    private CartResponseDTO convertToCartResponseDTO(Cart cart) {
        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(item -> new CartItemResponseDTO(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());

        return CartResponseDTO.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .items(items)
                .build();
    }
}
