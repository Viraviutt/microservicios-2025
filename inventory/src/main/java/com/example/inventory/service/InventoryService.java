package com.example.inventory.service;

import com.example.inventory.entity.Inventory;
import com.example.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public Flux<Inventory> getAllInventory() {
        return Flux.fromIterable(inventoryRepository.findAll());
    }

    public Mono<Optional<Inventory>> getInventoryById(Long id) {
        return Mono.just(inventoryRepository.findById(id));
    }

    public Mono<Inventory> createInventory(Inventory inventory) {
        return Mono.just(inventoryRepository.save(inventory));
    }
}