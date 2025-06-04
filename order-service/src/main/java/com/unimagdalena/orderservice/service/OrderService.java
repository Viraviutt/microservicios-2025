package com.unimagdalena.orderservice.service;

import com.unimagdalena.orderservice.client.InventoryClient;
import com.unimagdalena.orderservice.DTOs.InventoryUpdateRequest;
import com.unimagdalena.orderservice.entity.Order;
import com.unimagdalena.orderservice.respository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public Flux<Order> getAllOrders() {
        return Flux.defer(() -> Flux.fromIterable(orderRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic());
    }
    public Mono<Order> getOrderById(String id){
        return Mono.defer(() -> Mono.justOrEmpty(orderRepository.findById(id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Order> createOrder(Order order){
        return Mono.defer(() -> {
                    try {
                        // Luego actualizamos el inventario
                        InventoryUpdateRequest updateRequest = new InventoryUpdateRequest();
                        updateRequest.setId(order.getId());
                        updateRequest.setProductName(order.getProductName());
                        updateRequest.setQuantityToDecrease(order.getQuantity());

                        inventoryClient.updateInventory(updateRequest);
                        log.info("Inventario actualizado correctamente para el producto: {}", order.getProductName());
                    } catch (Exception e) {
                        log.error("Error al actualizar el inventario: {}", e.getMessage());
                        // Aquí podrías implementar un mecanismo de compensación o registrar el error
                    }
                    // Primero guardamos la orden
                    Order savedOrder = orderRepository.save(order);

                    return Mono.just(savedOrder);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Order> updateOrder(String id, Order order){
        return Mono.defer(() -> {
            if(orderRepository.existsById(id)){
                order.setId(id);
                return Mono.just(orderRepository.save(order));
            }
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteOrder(String id){
        return Mono.defer(() -> {
            if(orderRepository.existsById(id)){
                orderRepository.deleteById(id);
                return Mono.empty();
            }
            return Mono.error(new NoSuchElementException("Order not fount"));
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
