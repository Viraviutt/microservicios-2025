package com.example.product.repository;

import com.example.product.entity.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ExtendWith(SpringExtension.class)
@DataMongoTest
class ProductRepositoryTest {

    @Container
    static MongoDBContainer mongoContainer = new MongoDBContainer("mongo:6.0")
            .withExposedPorts(27017);

    @BeforeAll
    static void setupDatabaseProperties() {
        System.setProperty("MONGODB_URI", mongoContainer.getReplicaSetUrl());
    }

    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(50);
    }

    @Test
    void shouldSaveAndFindProductById() {
        Product savedProduct = productRepository.save(product);
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Test Product");
    }
}
