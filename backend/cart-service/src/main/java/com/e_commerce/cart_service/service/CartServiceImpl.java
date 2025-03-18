package com.e_commerce.cart_service.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_commerce.cart_service.client.ProductClient;
import com.e_commerce.cart_service.dto.CartItemRequestDTO;
import com.e_commerce.cart_service.dto.CartItemResponseDTO;
import com.e_commerce.cart_service.dto.CartResponseDTO;
import com.e_commerce.cart_service.dto.UpdateCartItemDTO;
import com.e_commerce.cart_service.dto.client.ProductResponseDTO;
import com.e_commerce.cart_service.exception.CartException;
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
    private final ProductClient productClient; // Tambahkan Feign Client

    // Mendapatkan keranjang berdasarkan userId
    @Override
    public CartResponseDTO getCartByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new CartException("User ID tidak boleh kosong");
        }

        try {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

            return convertToCartResponseDTO(cart);
        } catch (Exception e) {
            throw new CartException("Gagal mengambil keranjang: " + e.getMessage());
        }
    }

    // Menambahkan produk ke dalam keranjang
    @Override
    public CartResponseDTO addItemToCart(String userId, CartItemRequestDTO request) {
        if (request == null) {
            throw new CartException("Request tidak boleh null");
        }

        if (request.getQuantity() <= 0) {
            throw new CartException("Jumlah produk harus lebih dari 0");
        }

        try {
            Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUserId(userId);
                return cartRepository.save(newCart);
            });

            Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(),
                    request.getProductId());

            if (existingItem.isPresent()) {
                CartItem cartItem = existingItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
                cartItemRepository.save(cartItem);
            } else {
                CartItem newItem = CartItem.builder()
                        .cart(cart)
                        .productId(request.getProductId())
                        .quantity(request.getQuantity())
                        .build();
                cartItemRepository.save(newItem);
            }

            return convertToCartResponseDTO(cart);
        } catch (Exception e) {
            throw new CartException("Gagal menambahkan item ke dalam keranjang: " + e.getMessage());
        }
    }

    // Mengupdate jumlah produk dalam keranjang
    @Override
    public CartResponseDTO updateCartItemQuantity(String userId, UpdateCartItemDTO request) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new CartException("User ID tidak boleh kosong");
        }

        if (request == null || request.getProductId() == null) {
            throw new CartException("Request atau Product ID tidak boleh null");
        }

        try {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

            CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found in cart"));

            if (request.getQuantity() <= 0) {
                cartItemRepository.delete(cartItem);
            } else {
                cartItem.setQuantity(request.getQuantity());
                cartItemRepository.save(cartItem);
            }

            return convertToCartResponseDTO(cart);
        } catch (Exception e) {
            throw new CartException("Gagal memperbarui jumlah item di keranjang: " + e.getMessage());
        }
    }

    // Menghapus produk dari keranjang
    @Override
    public CartResponseDTO removeItemFromCart(String userId, String productId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new CartException("User ID tidak boleh kosong");
        }

        if (productId == null || productId.trim().isEmpty()) {
            throw new CartException("Product ID tidak boleh kosong");
        }

        try {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

            CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found in cart"));

            cartItemRepository.delete(cartItem);

            return convertToCartResponseDTO(cart);
        } catch (Exception e) {
            throw new CartException("Gagal menghapus item dari keranjang: " + e.getMessage());
        }
    }

    // Menghapus seluruh isi keranjang
    @Override
    public void clearCart(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new CartException("User ID tidak boleh kosong");
        }

        try {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

            if (cart.getItems().isEmpty()) {
                throw new CartException("Keranjang sudah kosong");
            }

            cartItemRepository.deleteAll(cart.getItems());
        } catch (Exception e) {
            throw new CartException("Gagal mengosongkan keranjang: " + e.getMessage());
        }
    }

    // Helper method untuk mengubah Cart ke CartResponseDTO
    private CartResponseDTO convertToCartResponseDTO(Cart cart) {
        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(item -> {
                    // 🔥 Ambil informasi produk dari product-service
                    ProductResponseDTO product = productClient.getProductById(item.getProductId());

                    return new CartItemResponseDTO(
                            item.getProductId(),
                            product.getName(), // Ambil nama dari ProductResponseDTO
                            product.getPrice(), // Ambil harga dari ProductResponseDTO
                            product.getImageUrl(), // Ambil gambar dari ProductResponseDTO
                            item.getQuantity());
                })
                .collect(Collectors.toList());

        return CartResponseDTO.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .items(items)
                .build();
    }

}
