package com.jonyshev.intershop.controller;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.CartAction;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.model.Order;
import com.jonyshev.intershop.model.PagingInfo;
import com.jonyshev.intershop.service.CartService;
import com.jonyshev.intershop.service.ItemService;
import com.jonyshev.intershop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CartService cartService;
    private final OrderService orderService;

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
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, getSort(sort));
        Page<Item> page = itemService.findItems(search, sort, pageable);

        List<ItemDto> itemDtos = itemService.mapToDto(page.getContent(), cartService);
        List<List<ItemDto>> items = chunkItems(itemDtos, 3);

        model.addAttribute("items", items);
        model.addAttribute("paging", new PagingInfo(pageNumber, pageSize, page.hasNext(), page.hasPrevious()));
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);

        return "main";
    }


    @PostMapping("/main/items/{id}")
    public String updateCartFromMain(@PathVariable Long id, @RequestParam CartAction action) {
        updateCartAction(id, action);
        return "redirect:/main/items";
    }

    @GetMapping("/cart/items")
    public String showCart(Model model) {
        model.addAttribute("items", cartService.getCartItemsDto());
        model.addAttribute("total", cartService.getTotalPrice());
        model.addAttribute("empty", cartService.isEmpty());
        return "cart";
    }

    @PostMapping("/cart/items/{id}")
    public String updateCartFromCart(@PathVariable Long id, @RequestParam CartAction action) {
        updateCartAction(id, action);
        return "redirect:/cart/items";
    }

    @PostMapping("/items/{id}")
    public String updateCartFromItemPage(@PathVariable Long id, @RequestParam CartAction action) {
        updateCartAction(id, action);
        return "redirect:/items/" + id;
    }

    @GetMapping("/items/{id}")
    public String getItemPage(@PathVariable Long id, Model model) {
        Item item = itemService.getItemById(id).orElseThrow(() -> new IllegalArgumentException("Item not found " + id));
        ItemDto itemDto = itemService.mapToDto(item, cartService);
        model.addAttribute("item", itemDto);
        return "item";
    }

    @PostMapping("/buy")
    public String buy() {
        List<Item> items = cartService.getCartItems();
        BigDecimal totalSum = cartService.getTotalPrice();
        Order order = orderService.createOrder(items, totalSum);
        cartService.clear();
        return "redirect:/orders/" + order.getId() + "?newOrder=true";
    }

    @GetMapping("/orders/{id}")
    public String showOrder(@PathVariable Long id,
                            @RequestParam(defaultValue = "false") boolean newOrder,
                            Model model) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found " + id));
        model.addAttribute("order", order);
        model.addAttribute("newOrder", newOrder);
        return "order";
    }

    @GetMapping("/orders")
    public String showOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders";
    }

    private void updateCartAction(Long id, CartAction action) {
        switch (action) {
            case PLUS -> cartService.addItem(id);
            case MINUS -> cartService.decreaseItem(id);
            case DELETE -> cartService.deleteItem(id);
        }
    }

    private List<List<ItemDto>> chunkItems(List<ItemDto> items, int rowSize) {
        List<List<ItemDto>> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i += rowSize) {
            result.add(items.subList(i, Math.min(i + rowSize, items.size())));
        }
        return result;
    }

    private Sort getSort(String sort) {
        return switch (sort) {
            case "ALPHA" -> Sort.by("title");
            case "PRICE" -> Sort.by("price");
            default -> Sort.unsorted();
        };
    }
}