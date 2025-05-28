package com.unimagdalena.paymentservice.controller;

import com.unimagdalena.paymentservice.entity.Payment;
import com.unimagdalena.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PaymentService paymentService;

    private Payment payment1;
    private Payment payment2;

    @BeforeEach
    void setUp() {
        // Initialize test data
        payment1 = new Payment("1", 45345L, BigDecimal.valueOf(100.00));
        payment2 = new Payment("2", 2002L, BigDecimal.valueOf(200.00));
    }

    @Test
    void getAllPayments_shouldReturnAllPayments() {
        when(paymentService.getAllPayments()).thenReturn(Flux.fromIterable(Arrays.asList(payment1, payment2)));

        webTestClient.get()
                .uri("/api/payments")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Payment.class)
                .hasSize(2)
                .contains(payment1, payment2);

        verify(paymentService).getAllPayments();
    }

    @Test
    void getPaymentById_whenPaymentExists_shouldReturnPayment() {
        when(paymentService.getPaymentById("1")).thenReturn(Mono.just(payment1));

        webTestClient.get()
                .uri("/api/payments/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Payment.class)
                .isEqualTo(payment1);

        verify(paymentService).getPaymentById("1");
    }

    @Test
    void getPaymentById_whenPaymentDoesNotExist_shouldReturnNotFound() {
        when(paymentService.getPaymentById("999")).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/payments/999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        verify(paymentService).getPaymentById("999");
    }

    @Test
    void createPayment_shouldReturnCreatedPayment() {
        Payment newPayment = new Payment(null, 3003L, BigDecimal.valueOf(300.00));
        Payment createdPayment = new Payment("3", 3003L, BigDecimal.valueOf(300.00));

        when(paymentService.createPayment(any(Payment.class))).thenReturn(Mono.just(createdPayment));

        webTestClient.post()
                .uri("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newPayment)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Payment.class)
                .isEqualTo(createdPayment);

        verify(paymentService).createPayment(any(Payment.class));
    }

    @Test
    void updatePayment_whenPaymentExists_shouldReturnUpdatedPayment() {
        Payment updatedPayment = new Payment("1", 1001L, BigDecimal.valueOf(150.00));

        when(paymentService.updatePayment(anyString(), any(Payment.class))).thenReturn(Mono.just(updatedPayment));

        webTestClient.put()
                .uri("/api/payments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedPayment)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Payment.class)
                .isEqualTo(updatedPayment);

        verify(paymentService).updatePayment(anyString(), any(Payment.class));
    }

    @Test
    void updatePayment_whenPaymentDoesNotExist_shouldReturnNotFound() {
        Payment updatedPayment = new Payment("999", 9999L, BigDecimal.valueOf(999.00));

        when(paymentService.updatePayment(anyString(), any(Payment.class))).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/api/payments/999")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedPayment)
                .exchange()
                .expectStatus().isNotFound();

        verify(paymentService).updatePayment(anyString(), any(Payment.class));
    }

    @Test
    void deletePayment_shouldReturnNoContent() {
        when(paymentService.deletePayment("1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/payments/1")
                .exchange()
                .expectStatus().isNoContent();

        verify(paymentService).deletePayment("1");
    }
}