package com.unimagdalena.orderservice.respository;

import com.unimagdalena.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
