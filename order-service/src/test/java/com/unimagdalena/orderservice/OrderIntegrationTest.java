package com.unimagdalena.orderservice;

import com.unimagdalena.orderservice.entity.Order;
import com.unimagdalena.orderservice.respository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public class OrderIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/orders";
    }

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void crudOperations_shouldWorkEndToEnd() {
        // 1. Create an order
        Order newOrder = new Order(null, "Integration Test Product", 5,
                new BigDecimal("49.99"), LocalDateTime.now());

        ResponseEntity<Order> createResponse = restTemplate.postForEntity(
                getBaseUrl(), newOrder, Order.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Order createdOrder = createResponse.getBody();
        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getProductName()).isEqualTo("Integration Test Product");

        // 2. Get the order by ID
        ResponseEntity<Order> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + createdOrder.getId(), Order.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getProductName()).isEqualTo("Integration Test Product");

        // 3. Get all orders
        ResponseEntity<List<Order>> getAllResponse = restTemplate.exchange(
                getBaseUrl(), HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Order>>() {});

        assertThat(getAllResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getAllResponse.getBody()).hasSize(1);

        // 4. Update the order
        Order updatedOrder = new Order(createdOrder.getId(), "Updated Product", 10,
                new BigDecimal("99.99"), LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Order> requestEntity = new HttpEntity<>(updatedOrder, headers);

        ResponseEntity<Order> updateResponse = restTemplate.exchange(
                getBaseUrl() + "/" + createdOrder.getId(),
                HttpMethod.PUT, requestEntity, Order.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().getProductName()).isEqualTo("Updated Product");
        assertThat(updateResponse.getBody().getQuantity()).isEqualTo(10);

        // 5. Delete the order
        restTemplate.delete(getBaseUrl() + "/" + createdOrder.getId());

        // Verify deletion
        ResponseEntity<Order> verifyDeleteResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + createdOrder.getId(), Order.class);
        assertThat(verifyDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
