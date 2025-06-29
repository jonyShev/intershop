package com.jonyshev.intershop.service;

import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.model.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllOrders();

    Optional<Order> findById(Long id);

    Order createOrder(List<Item> items, BigDecimal totalSum);
}
