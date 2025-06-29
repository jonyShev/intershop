package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.CartAction;
import com.jonyshev.intershop.model.Item;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SessionScope
public class CartServiceImpl implements CartService {

    private final ItemService itemService;

    private final Map<Long, Integer> cart = new HashMap<>();

    public CartServiceImpl(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public void addItem(Long id) {
        cart.put(id, cart.getOrDefault(id, 0) + 1);
    }

    @Override
    public void decreaseItem(Long id) {
        cart.computeIfPresent(id, (itemId, count) -> (count > 1) ? count - 1 : null);
    }

    @Override
    public void deleteItem(Long id) {
        cart.remove(id);
    }

    @Override
    public List<ItemDto> getCartItemsDto() {
        return cart.entrySet().stream()
                .map(entry -> {
                    Long id = entry.getKey();
                    Item item = itemService.getItemById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Item not found " + id));
                    return itemService.mapToDto(item, this);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getCartItems() {
        return cart.entrySet().stream()
                .map(entry -> {
                    Long id = entry.getKey();
                    Integer count = entry.getValue();
                    Item item = itemService.getItemById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Item not found " + id));
                    item.setCount(count);
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalPrice() {
        return cart.entrySet().stream()
                .map(entry -> {
                    Long id = entry.getKey();
                    Integer count = entry.getValue();
                    Item item = itemService.getItemById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Item not found " + id));

                    return item.getPrice().multiply(BigDecimal.valueOf(count));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean isEmpty() {
        return cart.isEmpty();
    }

    @Override
    public int getCountForItem(Long id) {
        return cart.getOrDefault(id, 0);
    }

    @Override
    public void clear() {
        cart.clear();
    }

    @Override
    public void updateCartAction(Long id, CartAction action) {
        switch (action) {
            case PLUS -> this.addItem(id);
            case MINUS -> this.decreaseItem(id);
            case DELETE -> this.deleteItem(id);
        }
    }
}
