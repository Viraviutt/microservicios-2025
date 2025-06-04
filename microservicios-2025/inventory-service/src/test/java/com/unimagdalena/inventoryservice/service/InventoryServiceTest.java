package com.unimagdalena.inventoryservice.service;

import com.unimagdalena.inventoryservice.entity.Inventory;
import com.unimagdalena.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void shouldGetAllInventoryItems() {
        // given
        Inventory inventory1 = Inventory.builder()
                .id(UUID.randomUUID().toString())
                .productName("Product 1")
                .quantity(10)
                .build();
        Inventory inventory2 = Inventory.builder()
                .id(UUID.randomUUID().toString())
                .productName("Product 2")
                .quantity(20)
                .build();
        List<Inventory> inventoryList = Arrays.asList(inventory1, inventory2);

        when(inventoryRepository.findAll()).thenReturn(inventoryList);

        // when
        Flux<Inventory> result = inventoryService.getAllInventoryItems();

        // then
        StepVerifier.create(result)
                .expectNext(inventory1)
                .expectNext(inventory2)
                .verifyComplete();
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    void shouldGetInventoryItemById() {
        // given
        String id = UUID.randomUUID().toString();
        Inventory inventory = Inventory.builder()
                .id(id)
                .productName("Product")
                .quantity(15)
                .build();

        when(inventoryRepository.findById(id)).thenReturn(Optional.of(inventory));

        // when
        Mono<Inventory> result = inventoryService.getInventoryItemById(id);

        // then
        StepVerifier.create(result)
                .expectNext(inventory)
                .verifyComplete();
        verify(inventoryRepository, times(1)).findById(id);
    }

    @Test
    void shouldReturnEmptyWhenInventoryItemNotFound() {
        // given
        String id = UUID.randomUUID().toString();
        when(inventoryRepository.findById(id)).thenReturn(Optional.empty());

        // when
        Mono<Inventory> result = inventoryService.getInventoryItemById(id);

        // then
        StepVerifier.create(result)
                .verifyComplete();
        verify(inventoryRepository, times(1)).findById(id);
    }

    @Test
    void shouldCreateInventoryItem() {
        // given
        Inventory inventory = Inventory.builder()
                .productName("New Product")
                .quantity(30)
                .build();

        Inventory savedInventory = Inventory.builder()
                .id(UUID.randomUUID().toString())
                .productName("New Product")
                .quantity(30)
                .build();

        when(inventoryRepository.save(any(Inventory.class))).thenReturn(savedInventory);

        // when
        Mono<Inventory> result = inventoryService.createInventoryItem(inventory);

        // then
        StepVerifier.create(result)
                .expectNextMatches(item ->
                        item.getId() != null &&
                                item.getProductName().equals("New Product") &&
                                item.getQuantity() == 30)
                .verifyComplete();
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void shouldUpdateInventoryItem() {
        // given
        String id = UUID.randomUUID().toString();
        Inventory inventory = Inventory.builder()
                .productName("Updated Product")
                .quantity(25)
                .build();

        Inventory updatedInventory = Inventory.builder()
                .id(id)
                .productName("Updated Product")
                .quantity(25)
                .build();

        when(inventoryRepository.existsById(id)).thenReturn(true);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(updatedInventory);

        // when
        Mono<Inventory> result = inventoryService.updateInventoryItem(id, inventory);

        // then
        StepVerifier.create(result)
                .expectNext(updatedInventory)
                .verifyComplete();
        verify(inventoryRepository, times(1)).existsById(id);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void shouldReturnEmptyWhenUpdateNonExistingItem() {
        // given
        String id = UUID.randomUUID().toString();
        Inventory inventory = Inventory.builder()
                .productName("Non-existing Product")
                .quantity(40)
                .build();

        when(inventoryRepository.existsById(id)).thenReturn(false);

        // when
        Mono<Inventory> result = inventoryService.updateInventoryItem(id, inventory);

        // then
        StepVerifier.create(result)
                .verifyComplete();
        verify(inventoryRepository, times(1)).existsById(id);
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void shouldDeleteInventoryItem() {
        // given
        String id = UUID.randomUUID().toString();
        doNothing().when(inventoryRepository).deleteById(id);

        // when
        Mono<Void> result = inventoryService.deleteInventoryItem(id);

        // then
        StepVerifier.create(result)
                .verifyComplete();
        verify(inventoryRepository, times(1)).deleteById(id);
    }
}
