package com.jonyshev.intershop.service;

import com.jonyshev.intershop.model.Order;
import com.jonyshev.intershop.model.OrderItem;
import com.jonyshev.intershop.repository.OrderItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceImplTest {

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    void findAllByOrderIdTest() {
        //given
        Order order = Order.builder().id(1L).build();
        OrderItem item1 = OrderItem.builder().orderId(order.getId()).itemId(10L).title("Item A").build();
        OrderItem item2 = OrderItem.builder().orderId(order.getId()).itemId(11L).title("Item B").build();

        //mock
        when(orderItemRepository.findAllByOrderId(order.getId())).thenReturn(Flux.just(item1, item2));
        //when
        Flux<OrderItem> result = orderItemService.findAllByOrderId(order.getId());
        //then
        StepVerifier.create(result)
                .expectNext(item1)
                .expectNext(item2)
                .verifyComplete();

        verify(orderItemRepository).findAllByOrderId(order.getId());
    }

}
