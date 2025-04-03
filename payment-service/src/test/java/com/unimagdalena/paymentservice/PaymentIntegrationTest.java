package com.unimagdalena.paymentservice;

import com.unimagdalena.paymentservice.entity.Payment;
import com.unimagdalena.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class PaymentIntegrationTest {

    // Define PostgreSQL container for integration tests
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("payment-integration-test-db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate; // Use TestRestTemplate instead of WebTestClient

    @Autowired
    private PaymentRepository paymentRepository;

    private String getRootUrl() {
        return "http://localhost:" + port + "/api/payments";
    }

    @BeforeEach
    void setUp() {
        // Clear database before each test
        paymentRepository.deleteAll();
    }

    @Test
    void crudOperations_shouldWorkEndToEnd() {
        // Create payment object
        Payment paymentToCreate = new Payment();
        paymentToCreate.setOrderId(1001L);
        paymentToCreate.setAmount(new BigDecimal("100.00"));

        // Test POST - Create payment
        ResponseEntity<Payment> postResponse = restTemplate.postForEntity(
                getRootUrl(), paymentToCreate, Payment.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Payment createdPayment = postResponse.getBody();
        assertThat(createdPayment).isNotNull();
        assertThat(createdPayment.getId()).isNotNull();
        String paymentId = createdPayment.getId();

        // Test GET by ID - Retrieve payment
        ResponseEntity<Payment> getResponse = restTemplate.getForEntity(
                getRootUrl() + "/" + paymentId, Payment.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(paymentId);
        assertThat(getResponse.getBody().getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));

        // Test GET all - Should include our payment
        ResponseEntity<Payment[]> getAllResponse = restTemplate.getForEntity(
                getRootUrl(), Payment[].class);
        assertThat(getAllResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getAllResponse.getBody()).hasSize(1);

        // Test PUT - Update payment
        Payment paymentToUpdate = new Payment();
        paymentToUpdate.setId(paymentId);
        paymentToUpdate.setOrderId(1001L);
        paymentToUpdate.setAmount(new BigDecimal("150.00"));

        HttpEntity<Payment> requestEntity = new HttpEntity<>(paymentToUpdate);
        ResponseEntity<Payment> putResponse = restTemplate.exchange(
                getRootUrl() + "/" + paymentId,
                HttpMethod.PUT,
                requestEntity,
                Payment.class);

        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(putResponse.getBody().getAmount()).isEqualByComparingTo(new BigDecimal("150.00"));

        // Test DELETE - Remove payment
        restTemplate.delete(getRootUrl() + "/" + paymentId);

        // Verify deletion
        ResponseEntity<Payment> getAfterDeleteResponse = restTemplate.getForEntity(
                getRootUrl() + "/" + paymentId, Payment.class);
        assertThat(getAfterDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getNonExistingPayment_shouldReturnNotFound() {
        ResponseEntity<Payment> response = restTemplate.getForEntity(
                getRootUrl() + "/non-existing-id", Payment.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateNonExistingPayment_shouldReturnNotFound() {
        Payment nonExistingPayment = new Payment();
        nonExistingPayment.setId("non-existing-id");
        nonExistingPayment.setOrderId(9999L);
        nonExistingPayment.setAmount(new BigDecimal("999.00"));

        HttpEntity<Payment> requestEntity = new HttpEntity<>(nonExistingPayment);
        ResponseEntity<Payment> response = restTemplate.exchange(
                getRootUrl() + "/non-existing-id",
                HttpMethod.PUT,
                requestEntity,
                Payment.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}