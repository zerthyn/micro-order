package org.rhydo.microorder.controllers;

import lombok.RequiredArgsConstructor;
import org.rhydo.microorder.dtos.OrderResponse;
import org.rhydo.microorder.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestHeader("X-User-ID") String userId) {
        OrderResponse orderResponse = orderService.createOrder(userId);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }
}
