package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.CartAction;
import com.jonyshev.intershop.model.CartActionForm;
import com.jonyshev.intershop.service.CartService;
import com.jonyshev.intershop.service.ReactiveOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReactiveCartController {

    private final CartService cartService;
    private final ReactiveOrderService reactiveOrderService;

    @GetMapping("/cart/items")
    public Mono<String> getCartItems(WebSession session, Model model) {
        Mono<List<ItemDto>> listMono = cartService.getCartItemsDto(session);
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

    @PostMapping("/cart/items/{id}")
    public Mono<String> updateCartFromCart(@PathVariable Long id,
                                           @ModelAttribute CartActionForm form,
                                           WebSession session) {
        CartAction action = CartAction.valueOf(form.getAction().toUpperCase());
        return cartService.updateCartAction(id, action, session)
                .then(Mono.just("redirect:/cart/items"));
    }

    @PostMapping("/buy")
    public Mono<String> buy(WebSession session) {
        return reactiveOrderService.getItemsAndTotal(session)
                .flatMap(tuple -> {
                    List<ItemDto> itemDtos = tuple.getT1();
                    BigDecimal total = tuple.getT2();
                    return reactiveOrderService.createOrder(itemDtos, total, session);
                })
                .map(order -> "redirect:/orders/" + order.getId() + "?newOrder=true");
    }
}
