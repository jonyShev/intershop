/*
package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.CartAction;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.model.PagingInfo;
import com.jonyshev.intershop.service.CartService;
import com.jonyshev.intershop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public String getAllItems(@RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "NO") String sort,
                              @RequestParam(defaultValue = "10") int pageSize,
                              @RequestParam(defaultValue = "1") int pageNumber,
                              Model model) {
        Pageable pageable = itemService.buildPageable(pageNumber, pageSize, sort);
        Page<Item> page = itemService.findItems(search, sort, pageable);

        List<ItemDto> itemDtos = itemService.mapToDto(page.getContent(), cartService);
        List<List<ItemDto>> items = itemService.chunkItems(itemDtos, 3);

        model.addAttribute("items", items);
        model.addAttribute("paging", new PagingInfo(pageNumber, pageSize, page.hasNext(), page.hasPrevious()));
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);

        return "main";
    }

    @PostMapping("/main/items/{id}")
    public String updateCartFromMain(@PathVariable Long id, @RequestParam CartAction action) {
        cartService.updateCartAction(id, action);
        return "redirect:/main/items";
    }

    @GetMapping("/items/{id}")
    public String getItemPage(@PathVariable Long id, Model model) {
        Item item = itemService.getItemById(id).orElseThrow(() -> new IllegalArgumentException("Item not found " + id));
        ItemDto itemDto = itemService.mapToDto(item, cartService);
        model.addAttribute("item", itemDto);
        return "item";
    }

    @PostMapping("/items/{id}")
    public String updateCartFromItemPage(@PathVariable Long id, @RequestParam CartAction action) {
        cartService.updateCartAction(id, action);
        return "redirect:/items/" + id;
    }
}*/
