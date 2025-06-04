package com.unimagdalena.productservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCreateRequest {
    private String productId;
    private String productName;
    private int quantity;

}

