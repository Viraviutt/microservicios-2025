package com.unimagdalena.orderservice.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdateRequest {
    private String id;
    private String productName;
    private Integer quantityToDecrease;
}