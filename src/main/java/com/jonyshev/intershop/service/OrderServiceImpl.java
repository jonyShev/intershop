package com.jonyshev.intershop.service;

import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.model.Order;
import com.jonyshev.intershop.model.OrderItem;
import com.jonyshev.intershop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional
    public Order createOrder(List<Item> items, BigDecimal totalSum) {
        List<OrderItem> orderItems = items.stream()
                .map(item -> OrderItem.builder()
                        .item(item)
                        .count(item.getCount())
                        .build())
                .toList();

        Order order = Order.builder()
                .items(orderItems)
                .totalSum(totalSum)
                .build();
        return orderRepository.save(order);
    }
}
