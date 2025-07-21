/*
package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.model.CartAction;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.model.Order;
import com.jonyshev.intershop.service.CartService;
import com.jonyshev.intershop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;

    @GetMapping("/cart/items")
    public String getCartItems(Model model) {
        model.addAttribute("items", cartService.getCartItemsDto());
        model.addAttribute("total", cartService.getTotalPrice());
        model.addAttribute("empty", cartService.isEmpty());
        return "cart";
    }

    @PostMapping("/cart/items/{id}")
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
    }
}*/
