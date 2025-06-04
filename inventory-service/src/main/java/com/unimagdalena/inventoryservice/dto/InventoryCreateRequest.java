package com.unimagdalena.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCreateRequest {
    private String productId;
    private String productName;
    private int quantity;
}

