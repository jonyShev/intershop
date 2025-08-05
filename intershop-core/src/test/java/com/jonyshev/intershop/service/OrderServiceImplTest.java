package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.dto.OrderWithItemsDto;
import com.jonyshev.intershop.model.Order;
import com.jonyshev.intershop.model.OrderItem;
import com.jonyshev.intershop.repository.OrderItemRepository;
import com.jonyshev.intershop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartService cartService;

    @Mock
    private WebSession session;

    @Test
    void createOrderTest() {
        //given
        ItemDto item1 = ItemDto.builder()
                .id(1L).title("Item 1").price(BigDecimal.valueOf(10)).count(2).build();

        ItemDto item2 = ItemDto.builder()
                .id(2L).title("Item 2").price(BigDecimal.valueOf(20)).count(1).build();

        BigDecimal totalSum = BigDecimal.valueOf(40);

        Order savedOrder = Order.builder()
                .id(1L)
                .totalSum(totalSum)
                .build();

        List<ItemDto> items = List.of(item1, item2);

        //mock
        when(orderRepository.save(any())).thenReturn(Mono.just(savedOrder));
        when(orderItemRepository.saveAll(anyList())).thenReturn(Flux.empty());
        when(cartService.clear(session)).thenReturn(Mono.empty());


        //when
        Mono<Order> result = orderService.createOrder(items, totalSum, session);
        //then
        StepVerifier.create(result)
                .expectNextMatches(order -> order.getId().equals(1L) && order.getTotalSum().equals(totalSum))
                .verifyComplete();

        verify(orderRepository).save(any());
        verify(orderItemRepository).saveAll(anyList());
        verify(cartService).clear(session);
    }

    @Test
    void findById_shouldReturnOrder_whenFound() {
        //given
        Order order = Order.builder()
                .id(1L)
                .totalSum(BigDecimal.valueOf(100))
                .build();
        //mock
        when(orderRepository.findById(anyLong())).thenReturn(Mono.just(order));

        //when
        Mono<Order> result = orderService.findById(order.getId());
        //then
        StepVerifier.create(result)
                .expectNextMatches(res -> res.getId().equals(order.getId()) &&
                        res.getTotalSum().equals(order.getTotalSum()))
                .verifyComplete();
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        //given
        Long orderId = 1L;
        //mock
        when(orderRepository.findById(anyLong())).thenReturn(Mono.empty());
        //when
        Mono<Order> result = orderService.findById(orderId);
        //then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Order not found " + orderId)
                )
                .verify();
    }

    @Test
    void getOrderWithItems() {
        //given
        List<Order> orders = List.of(
                Order.builder().id(1L).totalSum(BigDecimal.valueOf(100)).build(),
                Order.builder().id(2L).totalSum(BigDecimal.valueOf(200)).build()
        );
        OrderItem item1 = OrderItem.builder().orderId(1L).itemId(10L).title("Item A").build();
        OrderItem item2 = OrderItem.builder().orderId(1L).itemId(11L).title("Item B").build();
        OrderItem item3 = OrderItem.builder().orderId(2L).itemId(20L).title("Item C").build();
        //mock
        when(orderRepository.findAll()).thenReturn(Flux.fromIterable(orders));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(Flux.just(item1, item2));
        when(orderItemRepository.findAllByOrderId(2L)).thenReturn(Flux.just(item3));
        //when
        Mono<List<OrderWithItemsDto>> result = orderService.getOrderWithItems();
        //then
        StepVerifier.create(result)
                .assertNext(dtos -> {
                    assert dtos.size() == 2;

                    OrderWithItemsDto dto1 = dtos.get(0);
                    assert dto1.getId().equals(1L);
                    assert dto1.getTotalSum().equals(BigDecimal.valueOf(100));
                    assert dto1.getOrderItems().size() == 2;

                    OrderWithItemsDto dto2 = dtos.get(1);
                    assert dto2.getId().equals(2L);
                    assert dto2.getTotalSum().equals(BigDecimal.valueOf(200));
                    assert dto2.getOrderItems().size() == 1;
                })
                .verifyComplete();

    }
}
