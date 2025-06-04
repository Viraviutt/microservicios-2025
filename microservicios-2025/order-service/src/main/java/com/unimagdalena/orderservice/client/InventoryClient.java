package com.unimagdalena.orderservice.client;

import com.unimagdalena.orderservice.DTOs.InventoryResponse;
import com.unimagdalena.orderservice.DTOs.InventoryUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", path = "/api/inventory")
public interface InventoryClient {

    @GetMapping("/{productName}")
    InventoryResponse getInventoryByProductName(@PathVariable String productName);

    @PostMapping("/product/update")
    InventoryResponse updateInventory(@RequestBody InventoryUpdateRequest request);
}