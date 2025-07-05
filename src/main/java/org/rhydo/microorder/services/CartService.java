package org.rhydo.microorder.services;

import org.rhydo.microorder.dtos.CartItemRequest;
import org.rhydo.microorder.dtos.CartItemResponse;

import java.util.List;

public interface CartService {
    void addToCart(String userId, CartItemRequest cartItemRequest);

    void deleteCartItem(String userId, Long productId);

    List<CartItemResponse> getCartItems(String userId);

    void clearCart(Long userId);
}
