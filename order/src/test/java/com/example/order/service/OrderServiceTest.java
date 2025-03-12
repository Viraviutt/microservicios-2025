package com.example.order.service;

import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mock;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Test
    void shouldGetOrderById() {
        Order order = new Order();
        order.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        when(orderRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000001"))).thenReturn(java.util.Optional.of(order));

        Mono<Optional<Order>> found = orderService.getOrderById(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        assertThat(found).isNotNull();
        verify(orderRepository).findById(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }
}