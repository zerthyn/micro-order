package org.rhydo.microorder.controllers;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.rhydo.microorder.dtos.CartItemRequest;
import org.rhydo.microorder.dtos.CartItemResponse;
import org.rhydo.microorder.services.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    @PostMapping
    @RateLimiter(name = "myRateLimiter")
    public ResponseEntity<String> addToCart(@RequestHeader("X-User-ID") String userId,
                                            @RequestBody CartItemRequest cartItemRequest) {
        cartService.addToCart(userId, cartItemRequest);
        return new ResponseEntity<>("Added successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<String> deleteFromCart(@RequestHeader("X-User-ID") String userId,
                                                 @PathVariable Long productId) {
        cartService.deleteCartItem(userId, productId);
        return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems(@RequestHeader("X-User-ID") String userId) {
        List<CartItemResponse> cartItems =  cartService.getCartItems(userId);
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }
}
