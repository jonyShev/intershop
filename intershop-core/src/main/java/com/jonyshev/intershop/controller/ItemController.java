package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.model.CartAction;
import com.jonyshev.intershop.model.CartActionForm;
import com.jonyshev.intershop.model.PagingInfo;
import com.jonyshev.intershop.service.CartService;
import com.jonyshev.intershop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

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

    @GetMapping("/main/items")
    public Mono<String> getAllItems(@RequestParam(defaultValue = "") String search,
                                    @RequestParam(defaultValue = "NO") String sort,
                                    @RequestParam(defaultValue = "10") int pageSize,
                                    @RequestParam(defaultValue = "1") int pageNumber,
                                    WebSession session,
                                    Model model) {
        return itemService.getItemChunks(search, sort, pageSize, pageNumber, session)
                .doOnNext(chunks -> {
                    model.addAttribute("items", chunks);
                    model.addAttribute("search", search);
                    model.addAttribute("paging", new PagingInfo(
                            pageNumber,
                            pageSize,
                            chunks.stream().mapToInt(List::size).sum() == pageSize,
                            pageNumber > 1
                    ));
                })
                .thenReturn("main");
    }

    @PostMapping("/main/items/{id}")
    public Mono<String> updateCartFromMain(@PathVariable Long id,
                                           @ModelAttribute CartActionForm form,
                                           WebSession session) {
        CartAction action = CartAction.valueOf(form.getAction().toUpperCase());
        return cartService.updateCartAction(id, action, session)
                .thenReturn("redirect:/main/items");
    }

    @GetMapping("/items/{id}")
    public Mono<String> getItemPage(@PathVariable Long id, WebSession session, Model model) {
        return itemService.getItemDtoById(id, session)
                .doOnNext(itemDto -> model.addAttribute("item", itemDto))
                .thenReturn("item");
    }

    @PostMapping("/items/{id}")
    public Mono<String> updateCartFromItemPage(@PathVariable Long id,
                                               @ModelAttribute CartActionForm form,
                                               WebSession session) {
        CartAction action = CartAction.valueOf(form.getAction().toUpperCase());
        return cartService.updateCartAction(id, action, session)
                .thenReturn("redirect:/items/" + id);
    }

}