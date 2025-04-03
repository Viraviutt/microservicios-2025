package com.unimagdalena.orderservice.repository;

import com.unimagdalena.orderservice.entity.Order;
import com.unimagdalena.orderservice.respository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void saveOrder_shouldPersistOrder() {
        // Given
        Order order = new Order(null, "Test Product", 2, new BigDecimal("29.99"), LocalDateTime.now());

        // When
        Order savedOrder = orderRepository.save(order);

        // Then
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(entityManager.find(Order.class, savedOrder.getId())).isEqualTo(savedOrder);
    }

    @Test
    void findById_whenOrderExists_shouldReturnOrder() {
        // Given
        Order order = new Order(null, "Test Product", 1, new BigDecimal("19.99"), LocalDateTime.now());
        Order persistedOrder = entityManager.persist(order);

        // When
        Optional<Order> foundOrder = orderRepository.findById(persistedOrder.getId());

        // Then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getProductName()).isEqualTo("Test Product");
    }

    @Test
    void findById_whenOrderDoesNotExist_shouldReturnEmpty() {
        // When
        Optional<Order> result = orderRepository.findById("non-existent-id");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllOrders() {
        // Given
        entityManager.persist(new Order(null, "Product 1", 1, new BigDecimal("10.00"), LocalDateTime.now()));
        entityManager.persist(new Order(null, "Product 2", 2, new BigDecimal("20.00"), LocalDateTime.now()));

        // When
        List<Order> orders = orderRepository.findAll();

        // Then
        assertThat(orders).extracting("productName")
                .contains("Product 1", "Product 2");
    }

    @Test
    void deleteById_shouldRemoveOrder() {
        // Given
        Order order = entityManager.persist(new Order(null, "To Delete", 1, new BigDecimal("15.00"), LocalDateTime.now()));

        // When
        orderRepository.deleteById(order.getId());

        // Then
        assertThat(entityManager.find(Order.class, order.getId())).isNull();
    }

    @Test
    void existsById_whenOrderExists_shouldReturnTrue() {
        // Given
        Order order = entityManager.persist(new Order(null, "Exists", 1, new BigDecimal("15.00"), LocalDateTime.now()));

        // When
        boolean exists = orderRepository.existsById(order.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_whenOrderDoesNotExist_shouldReturnFalse() {
        // When
        boolean exists = orderRepository.existsById("non-existent-id");

        // Then
        assertThat(exists).isFalse();
    }
}