package com.unimagdalena.inventoryservice;

import com.unimagdalena.inventoryservice.entity.Inventory;
import com.unimagdalena.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebTestClient
public class InventoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("inventory-test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private InventoryRepository inventoryRepository;

    private String laptop1Id;
    private String laptop2Id;

    @BeforeEach
    void setup() {
        laptop1Id = UUID.randomUUID().toString();
        laptop2Id = UUID.randomUUID().toString();

        Inventory inventory1 = Inventory.builder()
                .id(laptop1Id)
                .productName("Laptop Gaming")
                .quantity(100)
                .build();

        Inventory inventory2 = Inventory.builder()
                .id(laptop2Id)
                .productName("Laptop Office")
                .quantity(0)
                .build();

        inventoryRepository.save(inventory1);
        inventoryRepository.save(inventory2);
    }

    @AfterEach
    void cleanup() {
        inventoryRepository.deleteAll();
    }

    @Test
    void shouldGetAllInventoryItems() {
        webTestClient.get().uri("/api/inventory")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Inventory.class)
                .hasSize(2);
    }

    @Test
    void shouldGetInventoryItemById() {
        webTestClient.get().uri("/api/inventory/{id}", laptop1Id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Inventory.class)
                .consumeWith(response -> {
                    Inventory inventory = response.getResponseBody();
                    assert inventory != null;
                    assert inventory.getId().equals(laptop1Id);
                    assert inventory.getProductName().equals("Laptop Gaming");
                    assert inventory.getQuantity() == 100;
                });
    }

    @Test
    void shouldCreateInventoryItem() {
        Inventory newInventory = Inventory.builder()
                .productName("Laptop Ultra")
                .quantity(50)
                .build();

        webTestClient.post().uri("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newInventory)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Inventory.class)
                .consumeWith(response -> {
                    Inventory savedInventory = response.getResponseBody();
                    assert savedInventory != null;
                    assert savedInventory.getId() != null;
                    assert savedInventory.getProductName().equals("Laptop Ultra");
                    assert savedInventory.getQuantity() == 50;
                });
    }

    @Test
    void shouldUpdateInventoryItem() {
        Inventory updatedInventory = Inventory.builder()
                .productName("Laptop Gaming Updated")
                .quantity(75)
                .build();

        webTestClient.put().uri("/api/inventory/{id}", laptop1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedInventory)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Inventory.class)
                .consumeWith(response -> {
                    Inventory inventory = response.getResponseBody();
                    assert inventory != null;
                    assert inventory.getId().equals(laptop1Id);
                    assert inventory.getProductName().equals("Laptop Gaming Updated");
                    assert inventory.getQuantity() == 75;
                });
    }

    @Test
    void shouldDeleteInventoryItem() {
        webTestClient.delete().uri("/api/inventory/{id}", laptop1Id)
                .exchange()
                .expectStatus().isNoContent();

        // Verify it's deleted
        webTestClient.get().uri("/api/inventory/{id}", laptop1Id)
                .exchange()
                .expectStatus().isNotFound();
    }
}
