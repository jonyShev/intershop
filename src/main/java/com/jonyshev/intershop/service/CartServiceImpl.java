package com.jonyshev.intershop.service;

import com.jonyshev.intershop.model.CartAction;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    private final static String CART_KEY = "CART";

    @Override
    public void addItem(Long id, WebSession session) {
        Map<Long, Integer> cart = getCart(session);
        cart.put(id, cart.getOrDefault(id, 0) + 1);
    }

    @Override
    public void decreaseItem(Long id, WebSession session) {
        Map<Long, Integer> cart = getCart(session);
        cart.computeIfPresent(id, (itemId, count) -> (count > 1) ? count - 1 : null);
    }

    @Override
    public void deleteItem(Long id, WebSession session) {
        Map<Long, Integer> cart = getCart(session);
        cart.remove(id);
    }

    @Override
    public Mono<Integer> getCountForItem(Long id, WebSession session) {
        Map<Long, Integer> cart = getCart(session);
        int count = cart.getOrDefault(id, 0);
        return Mono.just(count);
    }

    @Override
    public Mono<Void> updateCartAction(Long id, CartAction action, WebSession session) {
        Map<Long, Integer> cart = getCart(session);
        switch (action) {
            case PLUS -> addItem(id, session);
            case MINUS -> decreaseItem(id, session);
            case DELETE -> deleteItem(id, session);
        }
        return Mono.empty();
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCart(WebSession session) {
        return (Map<Long, Integer>) session.getAttributes().computeIfAbsent(CART_KEY, key -> new HashMap<Long, Integer>());
    }


   /* private final Map<Long, Integer> cart = new HashMap<>();

    public CartServiceImpl(ItemService itemService) {
        this.itemService = itemService;
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
        ў
        cart.clear();
    }

    @Override
    public void updateCartAction(Long id, CartAction action) {
        switch (action) {
            case PLUS -> this.addItem(id);
            case MINUS -> this.decreaseItem(id);
            case DELETE -> this.deleteItem(id);
        }
    }*/

}
