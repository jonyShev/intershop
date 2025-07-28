package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.dto.OrderWithItemsDto;
import com.jonyshev.intershop.model.Order;
import com.jonyshev.intershop.model.OrderItem;
import com.jonyshev.intershop.service.OrderItemService;
import com.jonyshev.intershop.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
@Import(OrderController.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderItemService orderItemService;

    @Test
    void showOrderTest() {
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setTotalSum(BigDecimal.valueOf(100));

        OrderItem item1 = OrderItem.builder().orderId(orderId).title("item1").build();
        OrderItem item2 = OrderItem.builder().orderId(orderId).title("item2").build();

        when(orderService.findById(orderId)).thenReturn(Mono.just(order));
        when(orderItemService.findAllByOrderId(orderId)).thenReturn(Flux.just(item1, item2));

        webTestClient.get()
                .uri("/orders/{id}?newOrder=true", orderId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String html = response.getResponseBody();
                    assert html != null;
                    assert html.contains("item1");
                    assert html.contains("item2");
                });
    }

    @Test
    void showOrdersTest() {
        Order order1 = new Order();
        order1.setId(1L);
        order1.setTotalSum(BigDecimal.valueOf(50));

        Order order2 = new Order();
        order2.setId(2L);
        order2.setTotalSum(BigDecimal.valueOf(120));

        var dto1 = OrderWithItemsDto.builder()
                .id(order1.getId())
                .totalSum(order1.getTotalSum())
                .orderItems(List.of(
                        OrderItem.builder().title("itemA").build()
                ))
                .build();

        var dto2 = OrderWithItemsDto.builder()
                .id(order2.getId())
                .totalSum(order2.getTotalSum())
                .orderItems(List.of(
                        OrderItem.builder().title("itemB").build()
                ))
                .build();

        when(orderService.getOrderWithItems()).thenReturn(Mono.just(List.of(dto1, dto2)));

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String html = response.getResponseBody();
                    assert html != null;
                    assert html.contains("itemA");
                    assert html.contains("itemB");
                });
    }
}
