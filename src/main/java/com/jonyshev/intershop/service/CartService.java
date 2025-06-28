package com.jonyshev.intershop.service;

import com.jonyshev.intershop.model.Item;

import java.util.List;

public interface CartService {

    void addItem(Long id);

    void decreaseItem(Long id);

    void deleteItem(Long id);

    List<Item> getCartItems();

    int getTotalPrice();

    boolean isEmpty();

    int getCountForItem(Long id);
}