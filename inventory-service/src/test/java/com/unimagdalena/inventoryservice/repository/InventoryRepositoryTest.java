package com.unimagdalena.inventoryservice.repository;

import com.unimagdalena.inventoryservice.entity.Inventory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class InventoryRepositoryTest {

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
    private InventoryRepository inventoryRepository;

    @Test
    void shouldSaveInventory() {
        // given
        String id = UUID.randomUUID().toString();
        Inventory inventory = Inventory.builder()
                .id(id)
                .productName("Laptop")
                .quantity(10)
                .build();

        // when
        Inventory savedInventory = inventoryRepository.save(inventory);

        // then
        assertThat(savedInventory).isNotNull();
        assertThat(savedInventory.getId()).isEqualTo(id);
        assertThat(savedInventory.getProductName()).isEqualTo("Laptop");
        assertThat(savedInventory.getQuantity()).isEqualTo(10);
    }

    @Test
    void shouldFindById() {
        // given
        String id = UUID.randomUUID().toString();
        Inventory inventory = Inventory.builder()
                .id(id)
                .productName("Monitor")
                .quantity(15)
                .build();
        inventoryRepository.save(inventory);

        // when
        Optional<Inventory> foundInventory = inventoryRepository.findById(id);

        // then
        assertThat(foundInventory).isPresent();
        assertThat(foundInventory.get().getProductName()).isEqualTo("Monitor");
        assertThat(foundInventory.get().getQuantity()).isEqualTo(15);
    }

    @Test
    void shouldDeleteById() {
        // given
        String id = UUID.randomUUID().toString();
        Inventory inventory = Inventory.builder()
                .id(id)
                .productName("Keyboard")
                .quantity(20)
                .build();
        inventoryRepository.save(inventory);

        // when
        inventoryRepository.deleteById(id);

        // then
        Optional<Inventory> deletedInventory = inventoryRepository.findById(id);
        assertThat(deletedInventory).isEmpty();
    }
}