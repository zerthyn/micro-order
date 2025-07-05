package org.rhydo.microorder.dtos;

import lombok.Data;
import org.rhydo.microorder.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private BigDecimal totalAmount;
    private OrderStatus status = OrderStatus.PENDING;
    private List<OrderItemDTO> items;
    private LocalDateTime createdAt;
}
