package com.jonyshev.intershop.service;

import com.jonyshev.intershop.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<Item> getAllItems();

    Optional<Item> getItemById(Long id);

    Item saveItem(Item item);

    void deleteItem(Long id);
}
