package org.rhydo.microorder.dtos;

import lombok.Data;
import org.rhydo.microecom.models.Product;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long id;
    private Product product;
    private Integer quantity;
    private BigDecimal price;

}
