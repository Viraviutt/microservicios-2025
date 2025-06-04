package com.unimagdalena.productservice;

import com.unimagdalena.productservice.entity.Product;
import com.unimagdalena.productservice.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebTestClient
public class ProductIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void cleanup() {
        productRepository.deleteAll(); // No block() necesario - ddmetodo síncrono
    }

    @Test
    void shouldCreateAndRetrieveProduct() {
        // Create product
        Product request = Product.builder()
                .name("Integration Test Product")
                .description("Created during integration test")
                .price(new BigDecimal("150.0"))
                .build();

        // Post request
        Product createdProduct = webTestClient.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Product.class)
                .returnResult()
                .getResponseBody();

        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getId()).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo("Integration Test Product");
        assertThat(createdProduct.getPrice()).isEqualByComparingTo(new BigDecimal("150.0"));

        // Verify product exists in database - usando métodos síncronos
        Optional<Product> storedProductOptional = productRepository.findById(createdProduct.getId());
        assertThat(storedProductOptional).isPresent();
        Product storedProduct = storedProductOptional.get();
        assertThat(storedProduct.getName()).isEqualTo("Integration Test Product");
        assertThat(storedProduct.getId()).isNotNull();

        // Get all products and verify the created one is there
        webTestClient.get()
                .uri("/api/products")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .hasSize(1)
                .contains(createdProduct);
    }
}