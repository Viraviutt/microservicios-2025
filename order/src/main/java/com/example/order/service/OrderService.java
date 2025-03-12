package com.example.order.service;

import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Flux<Order> getAllOrders() {
        return Flux.fromIterable(orderRepository.findAll());
    }

    public Mono<Optional<Order>> getOrderById(UUID id) {
        return Mono.just(orderRepository.findById(id));
    }

    public Mono<Order> createOrder(Order order) {
        return Mono.just(orderRepository.save(order));
    }
}