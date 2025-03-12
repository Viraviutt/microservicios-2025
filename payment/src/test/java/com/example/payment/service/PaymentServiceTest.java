package com.example.payment.service;

import com.example.payment.entity.Payment;
import com.example.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mock;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    @Test
    void shouldGetPaymentById() {
        Payment payment = new Payment();
        payment.setId(1L);
        when(paymentRepository.findById(1L)).thenReturn(java.util.Optional.of(payment));

        Mono<Optional<Payment>> found = paymentService.getPaymentById(1L);

        assertThat(found).isNotNull();
        verify(paymentRepository).findById(1L);
    }
}