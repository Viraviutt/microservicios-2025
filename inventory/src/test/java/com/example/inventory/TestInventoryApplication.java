package com.example.inventory;

import org.springframework.boot.SpringApplication;

public class TestInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.from(InventoryApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
