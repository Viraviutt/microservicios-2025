package com.example.order.controller;

import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        Order order = new Order();
        order.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        when(orderService.getOrderById(UUID.fromString("00000000-0000-0000-0000-000000000001"))).thenReturn(Mono.just(Optional.of(order)));
    }

    @Test
    void shouldReturnOrderWhenRequested() throws Exception {
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk());
    }
}