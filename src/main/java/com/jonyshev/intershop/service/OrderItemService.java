package com.jonyshev.intershop.service;

import com.jonyshev.intershop.model.OrderItem;
import reactor.core.publisher.Flux;

public interface OrderItemService {
    Flux<OrderItem> findAllByOrderId(Long orderId);
}
