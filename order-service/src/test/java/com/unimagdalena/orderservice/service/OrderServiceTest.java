package com.unimagdalena.orderservice.service;

import com.unimagdalena.orderservice.entity.Order;
import com.unimagdalena.orderservice.respository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order("1", "Producto Test", 2, new BigDecimal("29.99"), LocalDateTime.now());
    }

    @Test
    void getAllOrders_shouldReturnAllOrders() {
        // Given
        when(orderRepository.findAll()).thenReturn(java.util.Arrays.asList(
                testOrder,
                new Order("2", "Otro Producto", 1, new BigDecimal("19.99"), LocalDateTime.now())
        ));

        // When
        Flux<Order> result = orderService.getAllOrders();

        // Then
        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();

        verify(orderRepository).findAll();
    }

    @Test
    void getOrderById_whenOrderExists_shouldReturnOrder() {
        // Given
        when(orderRepository.findById("1")).thenReturn(Optional.of(testOrder));

        // When
        Mono<Order> result = orderService.getOrderById("1");

        // Then
        StepVerifier.create(result)
                .expectNext(testOrder)
                .verifyComplete();

        verify(orderRepository).findById("1");
    }

    @Test
    void getOrderById_whenOrderDoesNotExist_shouldReturnEmptyMono() {
        // Given
        when(orderRepository.findById("999")).thenReturn(Optional.empty());

        // When
        Mono<Order> result = orderService.getOrderById("999");

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(orderRepository).findById("999");
    }

    @Test
    void createOrder_shouldSaveAndReturnOrder() {
        // Given
        Order orderToCreate = new Order(null, "Nuevo Producto", 3, new BigDecimal("39.99"), null);
        Order savedOrder = new Order("1", "Nuevo Producto", 3, new BigDecimal("39.99"), LocalDateTime.now());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        Mono<Order> result = orderService.createOrder(orderToCreate);

        // Then
        StepVerifier.create(result)
                .expectNext(savedOrder)
                .verifyComplete();

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrder_whenOrderExists_shouldUpdateAndReturnOrder() {
        // Given
        Order updatedOrder = new Order("1", "Producto Actualizado", 5, new BigDecimal("49.99"), LocalDateTime.now());

        when(orderRepository.existsById("1")).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        // When
        Mono<Order> result = orderService.updateOrder("1", updatedOrder);

        // Then
        StepVerifier.create(result)
                .expectNext(updatedOrder)
                .verifyComplete();

        verify(orderRepository).existsById("1");
        verify(orderRepository).save(updatedOrder);
    }

    @Test
    void updateOrder_whenOrderDoesNotExist_shouldReturnEmptyMono() {
        // Given
        Order updatedOrder = new Order("999", "No Existe", 1, new BigDecimal("10.00"), LocalDateTime.now());

        when(orderRepository.existsById("999")).thenReturn(false);

        // When
        Mono<Order> result = orderService.updateOrder("999", updatedOrder);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(orderRepository).existsById("999");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void deleteOrder_whenOrderExists_shouldDeleteAndReturnEmptyMono() {
        // Given
        when(orderRepository.existsById("1")).thenReturn(true);
        doNothing().when(orderRepository).deleteById("1");

        // When
        Mono<Void> result = orderService.deleteOrder("1");

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(orderRepository).existsById("1");
        verify(orderRepository).deleteById("1");
    }

    @Test
    void deleteOrder_whenOrderDoesNotExist_shouldReturnEmptyMono() {
        // Given
        when(orderRepository.existsById("999")).thenReturn(false);

        // When
        Mono<Void> result = orderService.deleteOrder("999");

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NoSuchElementException)
                .verify();

        verify(orderRepository).existsById("999");
        verify(orderRepository, never()).deleteById(any());
    }
}