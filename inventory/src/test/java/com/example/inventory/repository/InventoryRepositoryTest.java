package com.example.inventory.repository;

import com.example.inventory.entity.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ExtendWith(SpringExtension.class)
@DataJpaTest
class InventoryRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private InventoryRepository inventoryRepository;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        inventory.setProductName("100L");
        inventory.setStock(200);
    }

    @Test
    void shouldSaveAndFindInventoryById() {
        Inventory savedInventory = inventoryRepository.save(inventory);
        Optional<Inventory> foundInventory = inventoryRepository.findById(savedInventory.getId());

        assertThat(foundInventory).isPresent();
        assertThat(foundInventory.get().getProductName()).isEqualTo("100L");
    }
}
