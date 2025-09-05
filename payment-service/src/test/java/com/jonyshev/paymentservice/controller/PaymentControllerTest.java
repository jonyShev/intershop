package com.jonyshev.paymentservice.controller;

import com.jonyshev.paymentservice.model.PayRequest;
import com.jonyshev.paymentservice.model.PayResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PaymentControllerTest {

    private PaymentController controller;

    @Test
    void pay_success_whenEnoughBalance() {
        controller = new PaymentController(new BigDecimal("500.00"), "RUB");
        PayRequest req = new PayRequest().amount(100.0);

        ResponseEntity<PayResponse> resp = controller
                .pay("user-1", Mono.just(req), mock(ServerWebExchange.class))
                .block();

        assertNotNull(resp);
        assertEquals(200, resp.getStatusCode().value());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().getSuccess());
    }

    @Test
    void pay_fail_whenNotEnoughBalance() {
        controller = new PaymentController(new BigDecimal("50.00"), "RUB");
        PayRequest req = new PayRequest().amount(100.0);

        ResponseEntity<PayResponse> resp = controller
                .pay("user-2", Mono.just(req), mock(ServerWebExchange.class))
                .block();

        assertNotNull(resp);
        assertEquals(402, resp.getStatusCode().value());
    }

}
