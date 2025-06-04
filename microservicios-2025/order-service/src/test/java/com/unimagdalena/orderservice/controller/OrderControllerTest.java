package com.unimagdalena.orderservice.controller;

import com.unimagdalena.orderservice.entity.Order;
import com.unimagdalena.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
public class OrderControllerTest {

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void shouldGetAllOrders() {
        // Given
        Order order1 = new Order("1", "Product1", 1, new BigDecimal("10.0"), LocalDateTime.now());
        Order order2 = new Order("2", "Product2", 2, new BigDecimal("20.0"), LocalDateTime.now());

        when(orderService.getAllOrders()).thenReturn(Flux.just(order1, order2));

        // When/Then
        webTestClient.get().uri("/api/orders")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Order.class)
                .hasSize(2)
                .contains(order1, order2);

        verify(orderService).getAllOrders();
    }

    @Test
    public void shouldGetOrderById() {
        // Given
        String id = "1";
        Order order = new Order(id, "Product1", 1, new BigDecimal("10.0"), LocalDateTime.now());

        when(orderService.getOrderById(id)).thenReturn(Mono.just(order));

        // When/Then
        webTestClient.get().uri("/api/orders/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .isEqualTo(order);

        verify(orderService).getOrderById(id);
    }

    @Test
    public void shouldReturn404WhenOrderNotFound() {
        // Given
        String id = "nonexistent";
        when(orderService.getOrderById(id)).thenReturn(Mono.empty());

        // When/Then
        webTestClient.get().uri("/api/orders/{id}", id)
                .exchange()
                .expectStatus().isNotFound();

        verify(orderService).getOrderById(id);
    }

    @Test
    public void shouldCreateOrder() {
        // Given
        Order orderToCreate = new Order(null, "New Product", 1, new BigDecimal("15.0"), null);
        Order createdOrder = new Order("1", "New Product", 1, new BigDecimal("15.0"), LocalDateTime.now());

        when(orderService.createOrder(any(Order.class))).thenReturn(Mono.just(createdOrder));

        // When/Then
        webTestClient.post().uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(orderToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Order.class)
                .isEqualTo(createdOrder);
    }
}
