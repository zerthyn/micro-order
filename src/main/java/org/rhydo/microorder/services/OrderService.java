package org.rhydo.microorder.services;

import org.rhydo.microorder.dtos.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(String userId);
}
