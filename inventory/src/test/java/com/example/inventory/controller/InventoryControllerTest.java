package com.example.inventory.controller;

import com.example.inventory.entity.Inventory;
import com.example.inventory.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    @Autowired
    private ObjectMapper objectMapper;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setProductName("100L");
        inventory.setStock(200);
    }

    @Test
    void shouldCreateInventory() throws Exception {
        when(inventoryService.createInventory(any(Inventory.class))).thenReturn(Mono.just(inventory));

        mockMvc.perform(MockMvcRequestBuilders.post("/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(100L));
    }

    @Test
    void shouldGetInventoryById() throws Exception {
        when(inventoryService.getInventoryById(1L)).thenReturn(Mono.just(Optional.of(inventory)));

        mockMvc.perform(MockMvcRequestBuilders.get("/inventory/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(100L));
    }
}
