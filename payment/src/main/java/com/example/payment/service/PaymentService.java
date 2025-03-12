package com.example.payment.service;


import com.example.payment.entity.Payment;
import com.example.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Flux<Payment> getAllPayments() {
        return Flux.fromIterable(paymentRepository.findAll());
    }

    public Mono<Optional<Payment>> getPaymentById(Long id) {
        return Mono.just(paymentRepository.findById(id));
    }

    public Mono<Payment> createPayment(Payment payment) {
        return Mono.just(paymentRepository.save(payment));
    }
}