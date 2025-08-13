package com.jonyshev.intershop.service;

import com.jonyshev.intershop.paymentservice.api.PaymentApi;
import com.jonyshev.intershop.paymentservice.model.BalanceResponse;
import com.jonyshev.intershop.paymentservice.model.PayRequest;
import com.jonyshev.intershop.paymentservice.model.PayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceClientTest {

    @Mock
    PaymentApi paymentApi;

    @Captor
    ArgumentCaptor<PayRequest> payRequestCaptor;

    PaymentServiceClient client;

    @BeforeEach
    void setUp() {
        client = new PaymentServiceClient(paymentApi, "EUR");
    }

    @Test
    void getBalance_shouldMapAmountToBigDecimal() {
        // given
        var balance = new BalanceResponse().amount(123.45);
        when(paymentApi.getBalance()).thenReturn(Mono.just(balance));

        // when & then
        StepVerifier.create(client.getBalance())
                .expectNext(new BigDecimal("123.45"))
                .verifyComplete();

        verify(paymentApi).getBalance();
    }

    @Test
    void pay_whenSuccess_shouldReturnTrue_andSendCorrectBody() {
        // given
        when(paymentApi.pay(any()))
                .thenReturn(Mono.just(new PayResponse().success(true)));

        // when
        var amount = new BigDecimal("99.99");

        // then
        StepVerifier.create(client.pay(amount))
                .expectNext(true)
                .verifyComplete();

        verify(paymentApi).pay(payRequestCaptor.capture());
        PayRequest sent = payRequestCaptor.getValue();
        assertThat(sent.getAmount()).isEqualTo(99.99);
        assertThat(sent.getCurrency()).isEqualTo("EUR");
    }

    @Test
    void pay_whenPaymentRequired402_shouldReturnFalse() {
        // given
        var ex = WebClientResponseException.create(
                HttpStatus.PAYMENT_REQUIRED.value(),
                "Payment Required",
                HttpHeaders.EMPTY,
                new byte[0],
                Charset.defaultCharset()
        );
        when(paymentApi.pay(any())).thenReturn(Mono.error(ex));

        // when & then
        StepVerifier.create(client.pay(new BigDecimal("10.00")))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void pay_whenOtherError_shouldPropagateError() {
        // given
        var ex = WebClientResponseException.create(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Boom",
                HttpHeaders.EMPTY,
                new byte[0],
                Charset.defaultCharset()
        );
        when(paymentApi.pay(any())).thenReturn(Mono.error(ex));

        // when & then
        StepVerifier.create(client.pay(new BigDecimal("10.00")))
                .expectError(WebClientResponseException.class)
                .verify();
    }
}
