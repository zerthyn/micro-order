package org.rhydo.microorder.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.rhydo.microorder.dtos.OrderResponse;
import org.rhydo.microorder.enums.OrderStatus;
import org.rhydo.microorder.exceptions.AppException;
import org.rhydo.microorder.models.CartItem;
import org.rhydo.microorder.models.Order;
import org.rhydo.microorder.models.OrderItem;
import org.rhydo.microorder.repositories.CartItemRepository;
import org.rhydo.microorder.repositories.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public OrderResponse createOrder(String userId) {
        // Look for user
//        User user = userRepository.findById(Long.valueOf(userId))
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", Long.valueOf(userId)));

        // Look for cartItem
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new AppException("No cartItems found ");
        }

        // Calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalPrice);
        order.setStatus(OrderStatus.CONFIRMED);

        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(
                        null,
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice(),
                        order
                )).toList();
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // Clear the cart
        cartService.clearCart(userId);

        rabbitTemplate.convertAndSend("order.exchange",
                "order.tracking",
                Map.of("orderId", savedOrder.getId(), "status", savedOrder.getStatus()));

        return modelMapper.map(savedOrder, OrderResponse.class);
    }
}
