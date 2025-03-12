package com.example.order.repository;

import com.example.order.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class OrderRepositoryTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        postgres.start();
    }

    @Test
    void shouldSaveAndRetrieveOrder() {
        Order order = new Order();
        order.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        orderRepository.save(order);

        assertThat(orderRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000001"))).isPresent();
    }
}