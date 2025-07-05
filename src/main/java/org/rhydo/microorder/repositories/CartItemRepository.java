package org.rhydo.microorder.repositories;

import org.rhydo.microorder.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByUserIdAndProductId(String userId, Long productId);

    List<CartItem> findByUserId(String userId);

    void deleteByUserId(String userId);
}
