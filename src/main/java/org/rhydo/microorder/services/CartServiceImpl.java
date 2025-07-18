package org.rhydo.microorder.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.rhydo.microorder.clients.ProductServiceClient;
import org.rhydo.microorder.clients.UserServiceClient;
import org.rhydo.microorder.dtos.CartItemRequest;
import org.rhydo.microorder.dtos.CartItemResponse;
import org.rhydo.microorder.dtos.ProductResponse;
import org.rhydo.microorder.dtos.UserResponse;
import org.rhydo.microorder.exceptions.AppException;
import org.rhydo.microorder.exceptions.ResourceNotFoundException;
import org.rhydo.microorder.models.CartItem;
import org.rhydo.microorder.repositories.CartItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;
    private final ModelMapper modelMapper;

    @Override
    public void addToCart(String userId, CartItemRequest cartItemRequest) {
        // Look for product
        ProductResponse product = productServiceClient.getProductDetails(cartItemRequest.getProductId());

        if (product == null) {
            throw new AppException("Product not found");
        }

        if (product.getStockQuantity() < cartItemRequest.getQuantity()) {
            throw new AppException("Product quantity exceeds stock quantity");
        }

        // Look for user
        UserResponse user = userServiceClient.getUserDetails(userId);

        if (user == null) {
            throw new AppException("User not found");
        }

        // Look for cartItem
        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, cartItemRequest.getProductId())
                .orElse(null);

        if (existingCartItem != null) {
            // Update quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemRequest.getQuantity());
        } else {
            // Create new cart item
            existingCartItem = new CartItem();
            existingCartItem.setUserId(userId);
            existingCartItem.setProductId(cartItemRequest.getProductId());
            existingCartItem.setQuantity(cartItemRequest.getQuantity());
        }

        // Save to repository
        existingCartItem.setPrice(BigDecimal.valueOf(1000));
        cartItemRepository.save(existingCartItem);
    }

    @Override
    public void deleteCartItem(String userId, Long productId) {
        // Look for product
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Look for user
//        User user = userRepository.findById(Long.valueOf(userId))
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", Long.valueOf(userId)));

        // Look for cartItem
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "Productid", productId));

        cartItemRepository.deleteById(cartItem.getId());
    }

    @Override
    public List<CartItemResponse> getCartItems(String userId) {
        // Look for user
//        User user = userRepository.findById(Long.valueOf(userId))
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", Long.valueOf(userId)));

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        return cartItems.stream()
                .map(cartItem -> modelMapper.map(cartItem, CartItemResponse.class))
                .toList();
    }

    @Override
    public void clearCart(String userId) {
        // Look for user
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        cartItemRepository.deleteByUserId(userId);
    }
}
