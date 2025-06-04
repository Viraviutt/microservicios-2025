package com.unimagdalena.productservice.respository;

import com.unimagdalena.productservice.entity.Product;
import com.unimagdalena.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
public class ProductRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveProduct() {
        // given
        Product product = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("100.0"))
                .build();

        // when
        Product savedProduct = productRepository.save(product);

        // then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
    }

    @Test
    void shouldFindProductById() {
        // given
        Product product = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal ("100.0"))
                .build();
        Product savedProduct = productRepository.save(product);

        // when
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Test Product");
    }
}
