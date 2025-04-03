package com.unimagdalena.inventoryservice.controller;

import com.unimagdalena.inventoryservice.entity.Inventory;
import com.unimagdalena.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(InventoryController.class)
public class InventoryControllerTest {

    @MockitoBean
    private InventoryService inventoryService;

    @Autowired
    private WebTestClient webTestClient;

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

        when(inventoryService.getAllInventoryItems())
                .thenReturn(Flux.fromIterable(Arrays.asList(inventory1, inventory2)));

        // when & then
        webTestClient.get().uri("/api/inventory")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Inventory.class)
                .hasSize(2)
                .contains(inventory1, inventory2);
    }

    @Test
    void shouldGetInventoryItemById() {
        // given
        String id = UUID.randomUUID().toString();
        Inventory inventory = Inventory.builder()
                .id(id)
                .productName("Test Product")
                .quantity(15)
                .build();

        when(inventoryService.getInventoryItemById(id))
                .thenReturn(Mono.just(inventory));

        // when & then
        webTestClient.get().uri("/api/inventory/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Inventory.class)
                .isEqualTo(inventory);
    }

    @Test
    void shouldReturnNotFoundForNonExistingItem() {
        // given
        String id = UUID.randomUUID().toString();
        when(inventoryService.getInventoryItemById(id))
                .thenReturn(Mono.empty());

        // when & then
        webTestClient.get().uri("/api/inventory/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldCreateInventoryItem() {
        // given
        String id = UUID.randomUUID().toString();
        Inventory inventory = Inventory.builder()
                .productName("New Product")
                .quantity(30)
                .build();

        Inventory savedInventory = Inventory.builder()
                .id(id)
                .productName("New Product")
                .quantity(30)
                .build();

        when(inventoryService.createInventoryItem(any(Inventory.class)))
                .thenReturn(Mono.just(savedInventory));

        // when & then
        webTestClient.post().uri("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventory)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Inventory.class)
                .isEqualTo(savedInventory);
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

        when(inventoryService.updateInventoryItem(anyString(), any(Inventory.class)))
                .thenReturn(Mono.just(updatedInventory));

        // when & then
        webTestClient.put().uri("/api/inventory/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventory)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Inventory.class)
                .isEqualTo(updatedInventory);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingItem() {
        // given
        String id = UUID.randomUUID().toString();
        Inventory inventory = Inventory.builder()
                .productName("Non-existing Product")
                .quantity(40)
                .build();

        when(inventoryService.updateInventoryItem(anyString(), any(Inventory.class)))
                .thenReturn(Mono.empty());

        // when & then
        webTestClient.put().uri("/api/inventory/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventory)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldDeleteInventoryItem() {
        // given
        String id = UUID.randomUUID().toString();
        when(inventoryService.deleteInventoryItem(id))
                .thenReturn(Mono.empty());

        // when & then
        webTestClient.delete().uri("/api/inventory/{id}", id)
                .exchange()
                .expectStatus().isNoContent();
    }
}
