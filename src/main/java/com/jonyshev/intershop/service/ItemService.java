package com.jonyshev.intershop.service;

import com.jonyshev.intershop.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<Item> getAllProducts();

    Optional<Item> getProductById(Long id);

    Item saveProduct(Item item);

    void deleteProduct(Long id);
}
