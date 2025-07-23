package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Order;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.util.List;

public interface ReactiveOrderService {

    Mono<Order> createOrder(List<ItemDto> items, BigDecimal totalSum, WebSession session);

    Mono<Tuple2<List<ItemDto>, BigDecimal>> getItemsAndTotal(WebSession session);

    Mono<Order> findById(Long orderId);
}