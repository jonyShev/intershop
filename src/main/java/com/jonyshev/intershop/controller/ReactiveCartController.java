package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReactiveCartController {

    private final CartService cartService;

    @GetMapping("/cart/items")
    public Mono<String> getCartItems(WebSession session, Model model) {
        Mono<List<ItemDto>> listMono = cartService.getCartItemsDto(session).collectList();
        Mono<BigDecimal> totalMono = cartService.getTotalPrice(session);
        boolean empty = cartService.isEmpty(session);

        return Mono.zip(listMono, totalMono)
                .doOnNext(tuple -> {
                    List<ItemDto> items = tuple.getT1();
                    BigDecimal total = tuple.getT2();

                    model.addAttribute("items", items);
                    model.addAttribute("total", total);
                    model.addAttribute("empty", empty);

                })
                .thenReturn("cart");
    }

    /*@PostMapping("/cart/items/{id}")
    public String updateCartFromCart(@PathVariable Long id, @RequestParam CartAction action) {
        cartService.updateCartAction(id, action);
        return "redirect:/cart/items";
    }

    @PostMapping("/buy")
    public String buy() {
        List<Item> items = cartService.getCartItems();
        BigDecimal totalSum = cartService.getTotalPrice();
        Order order = orderService.createOrder(items, totalSum);
        cartService.clear();
        return "redirect:/orders/" + order.getId() + "?newOrder=true";
    }*/
}
