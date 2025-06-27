package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.model.CartAction;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.model.PagingInfo;
import com.jonyshev.intershop.service.CartService;
import com.jonyshev.intershop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private final CartService cartService;

    @GetMapping("/")
    public String redirectToMainItems() {
        return "redirect:/main/items";
    }

    @GetMapping("main/items")
    public String getAllItems(Model model) {
        List<Item> items = itemService.getAllItems();
        model.addAttribute("items", items);

        PagingInfo paging = new PagingInfo(1, 10, false, false);

        model.addAttribute("paging", paging);

        model.addAttribute("search", "");
        model.addAttribute("sort", "NO");

        return "main";
    }

    @PostMapping("/main/items/{id}")
    public String updateCartFromMain(@PathVariable Long id, @RequestParam CartAction action) {
        switch (action) {
            case PLUS -> cartService.addItem(id);
            case MINUS -> cartService.decreaseItem(id);
            case DELETE -> cartService.deleteItem(id);
        }
        return "redirect:/main/items";
    }

    @GetMapping("/cart/items")
    public String showCart(Model model){
        model.addAttribute("items", cartService.getCartItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        model.addAttribute("empty", cartService.isEmpty());

        return "cart";
    }

    @PostMapping("/cart/items/{id}")
    public String updateCartFromCart(@PathVariable Long id, @RequestParam CartAction action){
        switch (action) {
            case PLUS -> cartService.addItem(id);
            case MINUS -> cartService.decreaseItem(id);
            case DELETE -> cartService.deleteItem(id);
        }

        return "redirect:/cart/items";
    }

    @PostMapping("/items/{id}")
    public String updateCartFromItemPage(@PathVariable Long id, @RequestParam CartAction action){
        switch (action) {
            case PLUS -> cartService.addItem(id);
            case MINUS -> cartService.decreaseItem(id);
            case DELETE -> cartService.deleteItem(id);
        }

        return "redirect:/items/" + id;
    }
}