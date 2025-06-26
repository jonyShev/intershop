package com.jonyshev.intershop.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;
import java.util.Map;

@Service
@SessionScope
public class CartServiceImpl implements CartService{

    private final Map<Long, Integer> cart = new HashMap<>();

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
}
