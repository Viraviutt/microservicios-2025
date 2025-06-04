package com.unimagdalena.inventoryservice.service;

import com.unimagdalena.inventoryservice.entity.Inventory;
import com.unimagdalena.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public Flux<Inventory> getAllInventoryItems() {
        return Flux.defer(() -> Flux.fromIterable(inventoryRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Inventory> getInventoryItemById(String id) {
        return Mono.defer(() -> Mono.justOrEmpty(inventoryRepository.findById(id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Inventory> createInventoryItem(Inventory inventory) {
        if (inventory.getId() == null || inventory.getId().isEmpty()) {
            inventory.setId(UUID.randomUUID().toString());
        }
        return Mono.defer(() -> Mono.just(inventoryRepository.save(inventory)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Inventory> updateInventoryItem(String id, Inventory inventory) {
        return Mono.defer(() -> {
            if (inventoryRepository.existsById(id)) {
                inventory.setId(id);
                return Mono.just(inventoryRepository.save(inventory));
            }
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteInventoryItem(String id) {
        return Mono.defer(() -> {
            inventoryRepository.deleteById(id);
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    public Mono<Inventory> getInventoryByProductName(String productName) {
        return Mono.defer(() -> Mono.justOrEmpty(inventoryRepository.findByProductName(productName)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Inventory> decreaseInventory(String productName, Integer quantityToDecrease) {
        return Mono.defer(() -> {
            Inventory inventory = inventoryRepository.findByProductName(productName);
            if (inventory == null) {
                throw new RuntimeException("Producto no encontrado en inventario: " + productName);
            }

            if (inventory.getQuantity() < quantityToDecrease) {
                throw new RuntimeException("Inventario insuficiente para: " + productName);
            }

            inventory.setQuantity(inventory.getQuantity() - quantityToDecrease);
            return Mono.just(inventoryRepository.save(inventory));
        }).subscribeOn(Schedulers.boundedElastic());
    }

}
