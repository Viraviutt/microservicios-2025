package com.example.inventory.service;

import com.example.inventory.entity.Inventory;
import com.example.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setProductName("100L");
        inventory.setStock(200);
    }

    @Test
    void shouldSaveInventory() {
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
        Mono<Inventory> savedInventory = inventoryService.createInventory(inventory);
                //.saveInventory(inventory);
        assertThat(savedInventory).isNotNull();
        assertThat(savedInventory.block().getProductName()).isEqualTo("100L");
    }

    @Test
    void shouldFindInventoryById() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        Mono<Optional<Inventory>> foundInventory = inventoryService.getInventoryById(1L);
        assertThat(foundInventory.block()).isPresent();
        assertThat(foundInventory.block().orElseThrow().getProductName()).isEqualTo("100L");
    }
}