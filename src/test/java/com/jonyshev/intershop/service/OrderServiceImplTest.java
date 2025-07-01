package com.jonyshev.intershop.service;

import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.model.Order;
import com.jonyshev.intershop.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllOrders() {
        //given
        Order order = Order.builder()
                .id(1L)
                .build();

        when(orderRepository.findAll()).thenReturn(List.of(order));

        //when
        List<Order> result = orderService.getAllOrders();

        //then
        assertThat(result).hasSize(1);
    }

    @Test
    void testFindByid() {
        //given
        Order order = Order.builder()
                .id(1L)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        //when
        Optional<Order> result = orderService.findById(1L);
        //then
        assertThat(result).isPresent();
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateOrder() {
        //given
        Item item = Item.builder()
                .id(1L)
                .count(2)
                .build();
        BigDecimal totalSum = BigDecimal.valueOf(1000);

        List<Item> items = List.of(item);

        Order order = Order.builder()
                .id(1L)
                .totalSum(totalSum)
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        //when
        Order result = orderService.createOrder(items, totalSum);
        //then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTotalSum()).isEqualTo(BigDecimal.valueOf(1000));
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}