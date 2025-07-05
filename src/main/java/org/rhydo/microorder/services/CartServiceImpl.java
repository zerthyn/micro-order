package org.rhydo.microorder.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.rhydo.microorder.dtos.CartItemRequest;
import org.rhydo.microorder.dtos.CartItemResponse;
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
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public void addToCart(String userId, CartItemRequest cartItemRequest) {
        // Look for product
        Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", cartItemRequest.getProductId()));

        if (product.getStockQuantity() < cartItemRequest.getQuantity()) {
            throw new AppException("Product quantity exceeds stock quantity");
        }

        // Look for user
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", Long.valueOf(userId)));

        // Look for cartItem
        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .orElse(null);

        if (existingCartItem != null) {
            // Update quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemRequest.getQuantity());
        } else {
            // Create new cart item
            existingCartItem = new CartItem();
            existingCartItem.setUser(user);
            existingCartItem.setProduct(product);
            existingCartItem.setQuantity(cartItemRequest.getQuantity());
        }

        // Save to repository
        existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
        cartItemRepository.save(existingCartItem);
    }

    @Override
    public void deleteCartItem(String userId, Long productId) {
        // Look for product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Look for user
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", Long.valueOf(userId)));

        // Look for cartItem
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "Productid", productId));

        cartItemRepository.deleteById(cartItem.getId());
    }

    @Override
    public List<CartItemResponse> getCartItems(String userId) {
        // Look for user
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", Long.valueOf(userId)));

        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

        return cartItems.stream()
                .map(cartItem -> modelMapper.map(cartItem, CartItemResponse.class))
                .toList();
    }

    @Override
    public void clearCart(Long userId) {
        // Look for user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        cartItemRepository.deleteByUserId(user.getId());
    }
}
