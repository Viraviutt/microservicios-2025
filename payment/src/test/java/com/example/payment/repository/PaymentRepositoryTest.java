package com.example.payment.repository;

import com.example.payment.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class PaymentRepositoryTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        postgres.start();
    }

    @Test
    void shouldSaveAndRetrievePayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        paymentRepository.save(payment);

        assertThat(paymentRepository.findById(1L)).isPresent();
    }
}