package org.rhydo.microorder.repositories;

import org.rhydo.microorder.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
