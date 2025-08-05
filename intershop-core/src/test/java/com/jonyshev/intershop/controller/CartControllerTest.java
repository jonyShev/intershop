package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Order;
import com.jonyshev.intershop.service.CartService;
import com.jonyshev.intershop.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private WebSession session;

    @Test
    void getCartItemsTest() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .title("Test Product")
                .description("desc")
                .imgPath("img.jpg")
                .price(BigDecimal.valueOf(15.5))
                .count(2)
                .build();

        List<ItemDto> items = List.of(itemDto);
        BigDecimal total = BigDecimal.valueOf(31.0);

        when(cartService.getCartItemsDto(any())).thenReturn(Mono.just(items));
        when(cartService.getTotalPrice(any())).thenReturn(Mono.just(total));
        when(cartService.isEmpty(any())).thenReturn(false);

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String html = response.getResponseBody();
                    assertNotNull(html);
                    assertTrue(html.contains("Test Product")); // можно настроить под шаблон
                });
    }

    @Test
    void buyTest() {
        // given
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .title("Test Item")
                .price(BigDecimal.valueOf(20))
                .count(2)
                .build();

        List<ItemDto> itemDtos = List.of(itemDto);
        BigDecimal total = BigDecimal.valueOf(40);

        Order order = new Order();
        order.setId(42L);
        order.setTotalSum(total);

        // mocks
        when(orderService.getItemsAndTotal(any())).thenReturn(Mono.just(Tuples.of(itemDtos, total)));
        when(orderService.createOrder(eq(itemDtos), eq(total), any()))
                .thenReturn(Mono.just(order));
        // when + then
        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", location ->
                        assertEquals("/orders/42?newOrder=true", location)
                );
    }
}
