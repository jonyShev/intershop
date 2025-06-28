package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    void addItem(Long id);

    void decreaseItem(Long id);

    void deleteItem(Long id);

    List<ItemDto> getCartItems();

    BigDecimal getTotalPrice();

    boolean isEmpty();

    int getCountForItem(Long id);
}