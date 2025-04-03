package com.unimagdalena.paymentservice.repository;

import com.unimagdalena.paymentservice.entity.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PaymentRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("payment-test-db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void createPayment_shouldPersistPayment() {
        // Given
        String id = UUID.randomUUID().toString();
        Payment payment = new Payment();
        payment.setId(id);
        payment.setOrderId(1001L);
        payment.setAmount(BigDecimal.valueOf(100.00));

        // When
        Payment savedPayment = paymentRepository.save(payment);

        // Then
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.getId()).isEqualTo(id);
        assertThat(savedPayment.getOrderId()).isEqualTo(1001L);
        assertThat(savedPayment.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
    }

    @Test
    void findById_withExistingId_shouldReturnPayment() {
        // Given
        String id = UUID.randomUUID().toString(); // Add ID manually
        Payment payment = new Payment();
        payment.setId(id); // Set the ID
        payment.setOrderId(2002L);
        payment.setAmount(BigDecimal.valueOf(200.00));
        Payment persistedPayment = entityManager.persistAndFlush(payment);

        // When
        Optional<Payment> foundPayment = paymentRepository.findById(persistedPayment.getId());

        // Then
        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getId()).isEqualTo(persistedPayment.getId());
        assertThat(foundPayment.get().getOrderId()).isEqualTo(2002L);
        assertThat(foundPayment.get().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(200.00));
    }

    @Test
    void findById_withNonExistingId_shouldReturnEmpty() {
        // Given
        Optional<Payment> result = paymentRepository.findById(UUID.randomUUID().toString());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllPayments() {
        // Given
        Payment payment1 = new Payment();
        payment1.setId(UUID.randomUUID().toString()); // Add ID manually
        payment1.setOrderId(1001L);
        payment1.setAmount(BigDecimal.valueOf(100.00));

        Payment payment2 = new Payment();
        payment2.setId(UUID.randomUUID().toString()); // Add ID manually
        payment2.setOrderId(2002L);
        payment2.setAmount(BigDecimal.valueOf(200.00));

        entityManager.persistAndFlush(payment1);
        entityManager.persistAndFlush(payment2);

        // When
        List<Payment> payments = paymentRepository.findAll();

        // Then
        assertThat(payments).hasSize(2);
    }

    @Test
    void updatePayment_shouldModifyExistingPayment() {
        // Given
        String id = UUID.randomUUID().toString(); // Add ID manually
        Payment payment = new Payment();
        payment.setId(id); // Set the ID
        payment.setOrderId(1001L);
        payment.setAmount(BigDecimal.valueOf(100.00));
        Payment savedPayment = entityManager.persistAndFlush(payment);

        // When
        BigDecimal newAmount = BigDecimal.valueOf(150.00);
        savedPayment.setAmount(newAmount);
        Payment updatedPayment = paymentRepository.save(savedPayment);

        // Then
        assertThat(updatedPayment.getAmount()).isEqualByComparingTo(newAmount);
        assertThat(updatedPayment.getId()).isEqualTo(savedPayment.getId());
    }

    @Test
    void deletePayment_shouldRemovePayment() {
        // Given
        String id = UUID.randomUUID().toString(); // Add ID manually
        Payment payment = new Payment();
        payment.setId(id); // Set the ID
        payment.setOrderId(1001L);
        payment.setAmount(BigDecimal.valueOf(100.00));
        Payment savedPayment = entityManager.persistAndFlush(payment);

        // When
        paymentRepository.deleteById(savedPayment.getId());

        // Then
        Optional<Payment> result = paymentRepository.findById(savedPayment.getId());
        assertThat(result).isEmpty();
    }
}