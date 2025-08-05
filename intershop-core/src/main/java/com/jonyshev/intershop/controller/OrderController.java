package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.model.Order;
import com.jonyshev.intershop.model.OrderItem;
import com.jonyshev.intershop.service.OrderItemService;
import com.jonyshev.intershop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @GetMapping("/orders/{id}")
    public Mono<String> showOrder(@PathVariable Long id,
                                  @RequestParam(defaultValue = "false") boolean newOrder,
                                  Model model) {
        Mono<Order> orderMono = orderService.findById(id);
        Flux<OrderItem> orderItemFlux = orderItemService.findAllByOrderId(id);


        return Mono.zip(orderMono, orderItemFlux.collectList())
                .doOnNext(turple -> {
                    model.addAttribute("order", turple.getT1());
                    model.addAttribute("orderItems", turple.getT2());
                    model.addAttribute("newOrder", newOrder);
                })
                .thenReturn("order");
    }

    @GetMapping("/orders")
    public Mono<String> showOrders(Model model) {
        return orderService.getOrderWithItems()
                .doOnNext(orders -> model.addAttribute("orders", orders))
                .thenReturn("orders");
    }
}
