package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Order;
import com.jonyshev.intershop.paymentservice.model.PayResponse;
import com.jonyshev.intershop.service.CartService;
import com.jonyshev.intershop.service.OrderService;
import com.jonyshev.intershop.service.PaymentServiceClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = CartController.class)
@ActiveProfiles("test")
class CartControllerTest {

    @TestConfiguration
    static class NoSecurity {
        @Bean
        SecurityWebFilterChain securityWebFilterChain(
                org.springframework.security.config.web.server.ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(ex -> ex.anyExchange().permitAll())
                    .build();
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private PaymentServiceClient paymentServiceClient;

    @Test
    void checkoutSuccess_redirectsToNewOrder() {
        // given
        var items = List.<ItemDto>of();
        var total = BigDecimal.valueOf(1999, 2); // 19.99

        when(orderService.getItemsAndTotal(any()))
                .thenReturn(Mono.just(Tuples.of(items, total)));

        var ok = new PayResponse().success(true);
        when(paymentServiceClient.pay(total)).thenReturn(Mono.just(ok));

        Order order = Mockito.mock(Order.class);
        when(order.getId()).thenReturn(42L);
        when(orderService.createOrder(eq(items), eq(total), any()))
                .thenReturn(Mono.just(order));

        // when / then
        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/orders/42?newOrder=true");
    }

    @Test
    void checkoutInsufficientFunds_redirectsBackWithError() {
        // given
        var items = List.<ItemDto>of();
        var total = BigDecimal.TEN;

        when(orderService.getItemsAndTotal(any()))
                .thenReturn(Mono.just(Tuples.of(items, total)));

        var fail = new PayResponse().success(false);
        when(paymentServiceClient.pay(total)).thenReturn(Mono.just(fail));

        // when / then
        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items?err=INSUFFICIENT_FUNDS");
    }

    @Test
    void checkoutPaymentServiceUnavailable_redirectsBackWithError() {
        // given
        var items = List.<ItemDto>of();
        var total = BigDecimal.valueOf(5000, 2);

        when(orderService.getItemsAndTotal(any()))
                .thenReturn(Mono.just(Tuples.of(items, total)));

        when(paymentServiceClient.pay(total))
                .thenReturn(Mono.error(new RuntimeException("Payment service down")));

        // when / then
        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items?err=PAYMENT_SERVICE_UNAVAILABLE");
    }
}
